package ch.ladestation.connectncharge.controller.pagecontroller.beginning;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

public class HomePageController implements ViewMixin<Game, ControllerBase<Game>>, PageController {
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane shadowPane;
    @FXML
    private Button startButton;

    @FXML
    private void handleStackMenuClick(ActionEvent event) {
        menuPane.setVisible(true);
        menuPane.setOpacity(1);
        shadowPane.setVisible(true);
        shadowPane.setOpacity(1);
    }

    @FXML
    private void handleMenuCloseButton(ActionEvent event) {
        menuPane.setVisible(false);
        menuPane.setOpacity(0);
        shadowPane.setVisible(false);
        shadowPane.setOpacity(0);
    }

    @FXML
    private void handleHelpButton(ActionEvent event) throws IOException {
        StageHandler.setLastFxmlPath(FilePath.HOMEPAGE.getFilePath());
        StageHandler.openStage(FilePath.HELPPAGE.getFilePath());
    }

    @FXML
    private void handleAdminButton(ActionEvent event) throws IOException {
        StageHandler.setLastFxmlPath(FilePath.HOMEPAGE.getFilePath());
        StageHandler.openStage(FilePath.ADMINPAGE.getFilePath());
    }

    @FXML
    private void handleHighScoreButton(ActionEvent event) throws IOException {
        StageHandler.openStage(FilePath.HIGHSCORE.getFilePath());
    }

    @FXML
    private void handleShadowAnchorPaneClick(ActionEvent event) {
        shadowPane.setVisible(true);
        shadowPane.setOpacity(0);
        menuPane.setVisible(false);
        menuPane.setOpacity(0);
    }

    @FXML
    private void handleCreditsButton(ActionEvent event) {
        StageHandler.setLastFxmlPath(FilePath.HOMEPAGE.getFilePath());
        StageHandler.openStage(FilePath.CREDITS.getFilePath());
    }

    @Override
    public void setController(ApplicationController controller) {
        init(controller);
    }

    @Override
    public void setupUiToActionBindings(ControllerBase<Game> controller) {
        var ctrl = (ApplicationController) controller;
        startButton.setOnMouseClicked(m -> {
            StageHandler.openStage(FilePath.EDGECLICKSCREEN.getFilePath());
            ctrl.startRound();
        });
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
