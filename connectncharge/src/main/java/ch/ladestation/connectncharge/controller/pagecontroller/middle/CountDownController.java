package ch.ladestation.connectncharge.controller.pagecontroller.middle;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CountDownController implements ViewMixin<Game, ControllerBase<Game>>, Initializable, PageController {

    private static final int COUNTDOWN_SECONDS = 4;
    @FXML
    private Label countDownText;

    private ApplicationController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startCountDown();
    }

    private void startCountDown() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        final Runnable runnable = new Runnable() {
            private int countdownStarter = COUNTDOWN_SECONDS;

            public void run() {
                Platform.runLater(() -> {
                    countDownText.setText(String.valueOf(countdownStarter));
                });
                countdownStarter--;

                if (countdownStarter == 0) {
                    Platform.runLater(() -> {
                        countDownText.setText("Los ...");
                    });
                } else if (countdownStarter < 0) {
                    Platform.runLater(() -> {
                        StageHandler.openStage(FilePath.GAMEPAGE.getFilePath());
                        controller.setCountdownFinished();
                    });
                    scheduler.shutdown();
                }
            }
        };
        scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
    }

    @Override
    public void setController(ApplicationController controller) {
        init(controller);
        this.controller = controller;
    }

    @Override
    public void initializeParts() {

    }

    @Override
    public void layoutParts() {

    }

    @Override
    public List<String> getStylesheets() {
        return null;
    }
}
