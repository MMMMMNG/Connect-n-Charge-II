package ch.ladestation.connectncharge.controller.pagecontroller.end;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreditsController implements ViewMixin<Game, ControllerBase<Game>>, Initializable, PageController {

    public static final int CREDITS_HEIGHT = 2874 - 150;
    public static final Duration CREDIT_WATCHTIME = Duration.minutes(1.2);
    @FXML
    VBox creditBox;

    TranslateTransition trans;

    public CreditsController() {
    }

    @FXML
    private void handleXCloseButton(ActionEvent event) throws IOException {
        String fxmlPath =
            StageHandler.getLastFxmlPath() != null ? StageHandler.getLastFxmlPath() : FilePath.HOMEPAGE.getFilePath();
        StageHandler.openStage(fxmlPath);
    }

    @Override
    public void setController(ApplicationController controller) {
        init(controller);
    }

    @Override
    public void layoutParts() {

    }

    @Override
    public List<String> getStylesheets() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trans = new TranslateTransition(CREDIT_WATCHTIME, creditBox);
        trans.setByY(-CREDITS_HEIGHT);
        trans.setInterpolator(Interpolator.EASE_OUT);
        trans.playFromStart();
    }

    @Override
    public void initializeParts() {
    }
}

