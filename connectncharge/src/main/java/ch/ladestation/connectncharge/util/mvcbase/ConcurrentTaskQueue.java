package ch.ladestation.connectncharge.util.mvcbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A device where tasks can be submitted for execution.
 * <p>
 * Execution is asynchronous - possibly in a different thread - but the sequence is kept stable, such that
 * for all tasks A and B: if B is submitted after A, B will only be executed after A is finished.
 * <p>
 * New tasks can be submitted while tasks are running.
 * <p>
 * Task submission itself is supposed to be thread-confined,
 * i.e. creation of the ConcurrentTaskQueue and task submission is expected to run in the same thread,
 * most likely the JavaFX UI Application Thread.
 *
 * @author Dierk Koenig
 */

public final class ConcurrentTaskQueue<R> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ExecutorService executor;
    private final ConcurrentLinkedQueue<Task<R>> buffer;
    private final Duration maxToDoTime;

    private boolean running = false; // for non-thread-confined submissions, we might need an AtomicBoolean

    public ConcurrentTaskQueue() {
        this(Duration.ofSeconds(5));
    }

    public ConcurrentTaskQueue(Duration maxToDoTime) {
        this.maxToDoTime = maxToDoTime;
        this.executor = Executors.newFixedThreadPool(1);  // use 2 for overlapping onDone with next to-do
        this.buffer = new ConcurrentLinkedQueue<>();
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void submit(Supplier<R> todo) {
        submit(todo, r -> {
        });
    }

    public synchronized void submit(Supplier<R> todo, Consumer<R> onDone) {
        var task = new Task<>(todo, onDone);
        buffer.add(task);
        log.trace("just submitted task={}", task.hashCode());
        execute();
    }

    private synchronized void execute() {
        log.trace("starting execute, running={}, size={}", running, buffer.size());
        final Task<R> task;
        if (running) {
            log.trace("stopping execute, cuz running={} (must've been true)", running);
            return;
        }
        task = buffer.poll();
        log.trace("grabby grabby from the queue, task={} size={}",
            task != null ? task.hashCode() : "null", buffer.size());
        if (task == null) {
            log.trace("returning 'cuz queue is empty");
            return;
        }
        log.trace("setting running={} to true", running);
        running = true;
        final Future<R> todoFuture = executor.submit(task.todo::get);
        log.trace("submitted todo for task={}", task.hashCode());

        Runnable onDoneRunnable = () -> {
            try {
                final R r = todoFuture.get(maxToDoTime.getSeconds(), TimeUnit.SECONDS);
                task.onDone.accept(r);
            } catch (Exception e) {
                log.warn("exception caught handling task={}: {}", task.hashCode(), e.getMessage());
                e.printStackTrace(); // todo: think about better exception handling
            } finally {
                log.trace("done with task={}, setting running={} to false", task.hashCode(), running);
                running = false;
                execute();
            }
        };
        executor.submit(onDoneRunnable);
        log.trace("submitted onDoneRunnable for task={}", task.hashCode());
    }

    private static class Task<T> {
        private final Supplier<T> todo;    // the return type of to-do ..
        private final Consumer<T> onDone;  // .. must match the input type of onDone

        Task(Supplier<T> todo, Consumer<T> onDone) {
            this.todo = todo;
            this.onDone = onDone;
        }
    }
}

