package ch.ladestation.connectncharge.controller.pagecontroller.end;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.model.text.HelpPage;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreditsController implements ViewMixin<Game, ControllerBase<Game>>, Initializable, PageController {

    @FXML
    private Button forward;
    @FXML
    private Button back;
    @FXML
    private Label lbltextHelp;
    private HelpPage currentPage;


    @FXML
    private void handleXCloseButton(ActionEvent event) throws IOException {
        String fxmlPath =
            StageHandler.getLastFxmlPath() != null ? StageHandler.getLastFxmlPath() : FilePath.HOMEPAGE.getFilePath();
        StageHandler.openStage(fxmlPath);
    }

    public CreditsController() {
        currentPage = HelpPage.WELCOME;
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
        setPage(HelpPage.WELCOME);
    }

    @Override
    public void initializeParts() {
    }

    public void goForward(ActionEvent actionEvent) {
        setPage(currentPage.getNext());
    }

    public void goBack(ActionEvent actionEvent) {
        setPage(currentPage.getPrevious());
    }

    private void setPage(HelpPage page) {
        currentPage = page;
        lbltextHelp.setText(page.getText());
    }
}

