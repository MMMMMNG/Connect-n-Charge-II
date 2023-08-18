package ch.ladestation.connectncharge.model.game.gameinfo;

import ch.ladestation.connectncharge.controller.ApplicationController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

public final class MyTimer {
    private static final int MAX_SECONDS_PER_ROUND = 3600;
    private static final int MINUTES_PER_SECOND = 60;
    public static final int ADD_TIME = 15;

    private static boolean isTimerRunning = false;
    private static int secondsElapsed = 0;
    private static int additionalTime = 0;
    private static Timer timer;
    private static Label timerLabel;
    private static ApplicationController controller;

    private MyTimer() {
        throw new AssertionError();
    }

    public static void start() {
        if (!isTimerRunning) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (secondsElapsed <= MAX_SECONDS_PER_ROUND) {
                        secondsElapsed++;
                        // Perform any desired actions based on the elapsed time
                        Platform.runLater(() -> {
                            controller.setEndTime(timeFormat(secondsElapsed));
                            timerLabel.setText("Zeit: " + controller.getModel().endTime.get());
                        });
                    }
                }
            };
            // Schedule the task to run every second (1000 milliseconds)
            timer.scheduleAtFixedRate(task, 0, 1000);
            isTimerRunning = true;
        }
    }

    public static void stop() {
        timer.cancel();
        MyTimer.secondsElapsed = 0;
        MyTimer.isTimerRunning = false;
        MyTimer.additionalTime = 0;
    }

    public static void setController(ApplicationController controller) {
        MyTimer.controller = controller;
    }

    public static void setTimerLabel(Label timerLabel) {
        MyTimer.timerLabel = timerLabel;
    }

    public static void addTime(int additionalTime, Button addTimeButton) {

        if (additionalTime != 0) {
            MyTimer.additionalTime += additionalTime;
            secondsElapsed += MyTimer.additionalTime;
        }
        addTimeButton.setText("Tipp +" + timeFormat(MyTimer.additionalTime + ADD_TIME));
    }

    private static String timeFormat(int allSeconds) {
        int seconds = allSeconds;
        int minutes = seconds / MINUTES_PER_SECOND;
        seconds %= MINUTES_PER_SECOND;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
