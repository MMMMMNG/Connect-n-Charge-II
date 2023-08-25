package ch.ladestation.connectncharge.util.mvcbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.time.Duration;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Base class for all Controllers.
 * <p>
 * The whole application logic is located in controller classes.
 * <p>
 * Controller classes work on and manage the Model. Models encapsulate the whole application state.
 * <p>
 * Controllers provide the whole core functionality of the application, so called 'Actions'
 * <p>
 * Execution of Actions is asynchronous. The sequence is kept stable, such that
 * for all actions A and B: if B is submitted after A, B will only be executed after A is finished.
 */
public abstract class ControllerBase<M> {

    // the model managed by this Controller. Only subclasses have direct access
    protected final M model;
    protected final Logger log = LoggerFactory.getLogger(getClass().getName());
    private ConcurrentTaskQueue<M> actionQueue;

    /**
     * Controller needs a Model.
     *
     * @param model Model managed by this Controller
     */
    protected ControllerBase(M model) {
        Objects.requireNonNull(model);

        this.model = model;
    }

    public static <V> V[] arrayRemove(V[] theArray, V toRemove) {
        final var clazz = theArray.getClass().getComponentType();
        IntFunction<V[]> generator = length -> (V[]) Array.newInstance(clazz, length);
        return Arrays.stream(theArray).filter(e -> !e.equals(toRemove)).toArray(generator);
    }

    public static <V> V[] arrayAdd(V[] theArray, V toAdd) {
        var length = theArray.length;
        V[] newArr = (V[]) Array.newInstance(theArray.getClass().getComponentType(), length + 1);
        System.arraycopy(theArray, 0, newArr, 0, length);
        newArr[length] = toAdd;
        return newArr;
    }

    public void shutdown() {
        if (null != actionQueue) {
            actionQueue.shutdown();
            actionQueue = null;
        }
    }

    /**
     * If anything needs to be run once at startup from the controller
     */
    public void startUp() {
    }

    /**
     * Schedule the given action for execution in strict order in external thread, asynchronously.
     * <p>
     * onDone is called as soon as action is finished
     */
    protected void async(Supplier<M> action, Consumer<M> onDone) {
        if (null == actionQueue) {
            log.trace("new actionqueue");
            actionQueue = new ConcurrentTaskQueue<>();

        }
        actionQueue.submit(action, onDone);
    }

    /**
     * Schedule the given action for execution in strict order in external thread, asynchronously.
     */
    protected void async(Runnable todo) {
        async(() -> {
            todo.run();
            return model;
        }, m -> {
        });
    }

    /**
     * @param action Schedule the given action after all the actions already scheduled have finished.
     */
    public void runLater(Consumer<M> action) {
        async(() -> model, action);
    }

    /**
     * Intermediate solution for TestCase support.
     * <p>
     * Best solution would be that 'action' of 'runLater' is executed on calling thread.
     * <p>
     * Waits until all current actions in actionQueue are completed.
     * <p>
     * In most cases it's wrong to call this method from within an application.
     */
    public void awaitCompletion() {
        if (actionQueue == null) {
            log.warn("awaitCompletion() was skipped cuz actionQueue is null");
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        log.trace("starting await completion {}", this.hashCode());
        runLater(m -> latch.countDown());
        try {
            //noinspection ResultOfMethodCallIgnored
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException("CountDownLatch was interrupted");
        }
        log.trace("finished await completion {}", this.hashCode());
    }

    /**
     * Only the other base classes 'ViewMixin' and 'PUI_Base' need access, therefore it's 'package private'
     * ...except when trying to mock this. so nvm.
     *
     * @return the model of this controller
     */
    public M getModel() {
        return model;
    }

    /**
     * Even for setting a value the controller is responsible.
     * <p>
     * No application specific class can access ObservableValue.setValue
     * <p>
     * Value is set asynchronously.
     */
    protected <V> void setValue(ObservableValue<V> observableValue, V newValue) {
        log.trace("scheduling setValue({}, {})", observableValue.toString(), newValue);
        async(() -> observableValue.setValue(newValue));
    }

    /**
     * Even for setting values in the array the controller is responsible.
     * <p>
     * No application specific class can access ObservableValue.setValues
     * <p>
     * Values are set asynchronously.
     */
    protected <V> void setValues(ObservableArray<V> observableArray, V[] newValues) {
        async(() -> observableArray.setValues(newValues));
    }

    /**
     * Even for setting a value in the array the controller is responsible.
     * <p>
     * No application specific class can access ObservableValue.setValue
     * <p>
     * Value is set asynchronously.
     */
    protected <V> void setValue(ObservableArray<V> observableArray, int position, V newValue) {
        async(() -> observableArray.setValue(position, newValue));
    }

    protected <V> V get(ObservableValue<V> observableValue) {
        return observableValue.getValue();
    }

    protected <V> V[] get(ObservableArray<V> observableArray) {
        return observableArray.getValues();
    }

    protected <V> V get(ObservableArray<V> observableArray, int position) {
        return observableArray.getValue(position);
    }

    /**
     * Convenience method to toggle a {@code ObservableValue<Boolean>}
     */
    protected void toggleValue(ObservableValue<Boolean> observableValue) {
        async(() -> observableValue.setValue(!observableValue.getValue()));
    }

    /**
     * Convenience method to toggle a {@code ObservableArray<Boolean>} at position x
     */
    protected void toggle(ObservableArray<Boolean> observableArray, int position) {
        async(() -> observableArray.setValue(position, !observableArray.getValue(position)));
    }

    /**
     * Convenience method to increase a {@code ObservableValue<Integer>} by 1
     */
    protected void increaseValue(ObservableValue<Integer> observableValue) {
        async(() -> observableValue.setValue(observableValue.getValue() + 1));
    }

    /**
     * Convenience method to increase a {@code ObservableArray<Integer>} by 1 at position x
     */
    protected void increase(ObservableArray<Integer> observableArray, int position) {
        async(() -> observableArray.setValue(position, observableArray.getValue(position) + 1));
    }

    /**
     * Convenience method to decrease a {@code ObservableValue<Integer>} by 1
     */
    protected void decreaseValue(ObservableValue<Integer> observableValue) {
        async(() -> observableValue.setValue(observableValue.getValue() - 1));
    }

    /**
     * Convenience method to decrease a {@code ObservableArray<Integer>} by 1 at position x
     */
    protected void decrease(ObservableArray<Integer> observableArray, int position) {
        async(() -> observableArray.setValue(position, observableArray.getValue(position) - 1));
    }

    /**
     * Convenience method to remove an element from an {@code ObservableArray<V>}
     *
     * @param observableArray the array that will be affected
     * @param elementToRemove the element to remove
     * @param <V>             the type of the array and element
     */
    protected <V> void remove(ObservableArray<V> observableArray, V elementToRemove) {
        async(() -> observableArray.setValues(arrayRemove(get(observableArray), elementToRemove)));
    }

    /**
     * Convenience method to add an element to an {@code ObservableArray<V>}
     *
     * @param observableArray the array that will be affected
     * @param elementToAdd    the element to add
     * @param <V>             the type of the array and element
     */
    protected <V> void add(ObservableArray<V> observableArray, V elementToAdd) {
        async(() -> observableArray.setValues(arrayAdd(observableArray.getValues(), elementToAdd)));
    }

    /**
     * Convenience method to add an element to an {@code ObservableArray<V>} but
     * only if the array doesn't already contain that element
     *
     * @param observableArray the array that will be affected
     * @param elementToAdd    the element to add
     * @param <V>             the type of the array and element
     */
    protected <V> void addUnique(ObservableArray<V> observableArray, V elementToAdd) {
        async(() -> {
            V[] vals = observableArray.getValues();
            if (!Arrays.asList(vals).contains(elementToAdd)) {
                observableArray.setValues(arrayAdd(vals, elementToAdd));
            }
        });
    }

    /**
     * Utility function to pause execution of actions for the specified amount of time.
     * <p>
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param duration time to sleep
     */
    protected void pauseExecution(Duration duration) {
        async(() -> {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Use this if you need to update several ObservableValues in one async call.
     * <p>
     * Use 'set', 'increase', 'decrease' or 'toggle' to get an appropriate Setter
     */
    protected void updateModel(Setter<?>... setters) {
        async(() -> {
            for (Setter<?> setter : setters) {
                setter.setValue();
            }
        });
    }

    protected <V> Setter<V> set(ObservableValue<V> observableValue, V value) {
        return new Setter<>(observableValue, () -> value);
    }

    protected Setter<Integer> increase(ObservableValue<Integer> observableValue) {
        return new Setter<>(observableValue, () -> get(observableValue) + 1);
    }

    protected Setter<Integer> decrease(ObservableValue<Integer> observableValue) {
        return new Setter<>(observableValue, () -> get(observableValue) - 1);
    }

    protected Setter<Boolean> toggle(ObservableValue<Boolean> observableValue) {
        return new Setter<>(observableValue, () -> !get(observableValue));
    }

    protected <V> ArraySetter<V> set(ObservableArray<V> observableArray, V[] values) {
        return new ArraySetter<>(observableArray, () -> values);
    }

    protected static final class Setter<V> {
        private final ObservableValue<V> observableValue;

        // supplier is used here to get the value at execution time and not at registration time
        private final Supplier<V> valueSupplier;

        private Setter(ObservableValue<V> observableValue, Supplier<V> valueSupplier) {
            this.observableValue = observableValue;
            this.valueSupplier = valueSupplier;
        }

        void setValue() {
            observableValue.setValue(valueSupplier.get());
        }
    }

    protected static final class ArraySetter<V> {
        private final ObservableArray<V> observableArray;
        private final Supplier<V[]> valuesSupplier;

        private ArraySetter(ObservableArray<V> observableArray, Supplier<V[]> valuesSupplier) {
            this.observableArray = observableArray;
            this.valuesSupplier = valuesSupplier;
        }

        void setValue() {
            observableArray.setValues(valuesSupplier.get());
        }
    }
}
