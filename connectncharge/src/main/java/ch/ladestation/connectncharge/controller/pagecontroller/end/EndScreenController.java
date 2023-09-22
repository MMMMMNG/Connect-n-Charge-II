package ch.ladestation.connectncharge.controller.pagecontroller.end;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.controller.pagecontroller.middle.GamePageController;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EndScreenController implements Initializable, ViewMixin<Game, ControllerBase<Game>>, PageController {
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane shadowPane;
    @FXML
    private Button btnHighscore;
    @FXML
    private Button btnPlayAgain;
    @FXML
    private Button btnMenu;
    @FXML
    private Label lblTime;

    @FXML
    private Parent root;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    private ApplicationController controller;

    //private final String endTime = String.valueOf(controller.scoreForEndScreen());

    @FXML
    public void handlePlayAgainButton(ActionEvent actionEvent) throws IOException {
        controller.playAgain();
        StageHandler.openStage(FilePath.EDGECLICKSCREEN.getFilePath());
    }

    @FXML
    public void handleNameInputButton(ActionEvent actionEvent) throws IOException {
        showNameInputScreen(actionEvent);
    }

    @FXML
    private void handleStackMenuClick(ActionEvent event) {
        menuPane.setVisible(true);
        menuPane.setOpacity(1);
        shadowPane.setVisible(true);
        shadowPane.setOpacity(1);
    }

    @FXML
    private void handleShadowAnchorPaneClick(ActionEvent event) {
        shadowPane.setVisible(false);
        shadowPane.setOpacity(0);
        menuPane.setVisible(false);
        menuPane.setOpacity(0);
    }

    @FXML
    private void handleMenuCloseButton(ActionEvent event) {
        menuPane.setVisible(false);
        menuPane.setOpacity(0);
        shadowPane.setVisible(false);
        shadowPane.setOpacity(0);
    }

    public void showNameInputScreen(ActionEvent actionEvent) throws IOException {
        StageHandler.openStage(FilePath.NAMEINPUT.getFilePath());
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    @Override
    public void setupModelToUiBindings(Game model) {
        lblTime.textProperty().bind(model.endTime);
        GamePageController.setPublicEndTime(model.endTime.get());
    }
}
