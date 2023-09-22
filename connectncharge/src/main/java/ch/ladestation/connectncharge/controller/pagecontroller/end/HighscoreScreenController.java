package ch.ladestation.connectncharge.controller.pagecontroller.end;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.controller.pagecontroller.middle.GamePageController;
import ch.ladestation.connectncharge.model.game.gameinfo.HighScorePlayer;
import ch.ladestation.connectncharge.model.game.gameinfo.HighScoreTable;
import ch.ladestation.connectncharge.model.game.gameinfo.Player;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.services.file.TextFileEditor;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;

public class HighscoreScreenController implements ViewMixin<Game, ControllerBase<Game>>, PageController {
    ApplicationController controller;
    @FXML
    private Button btnPlayAgain;
    @FXML
    private Button btnBonus;
    @FXML
    private ImageView imgHome;
    @FXML
    private TableView<HighScorePlayer> tableView;
    @FXML
    private TableColumn<HighScorePlayer, Integer> rankColumn;
    @FXML
    private TableColumn<HighScorePlayer, String> nameColumn;
    @FXML
    private TableColumn<HighScorePlayer, String> timeColumn;
    @FXML
    private TableView<HighScorePlayer> restTableView;
    @FXML
    private TableColumn<HighScorePlayer, Integer> restRankColumn;
    @FXML
    private TableColumn<HighScorePlayer, String> restNameColumn;
    @FXML
    private TableColumn<HighScorePlayer, String> restTimeColumn;
    private String endTime = GamePageController.getPublicEndTime();
    private String playerName = NameInputController.getCurrentName();

    @FXML
    public void initialize() {
        HighScoreTable.initColumns(rankColumn, nameColumn, timeColumn);
        HighScoreTable.initColumns(restRankColumn, restNameColumn, restTimeColumn);
        HighScoreTable.initRowHeight(tableView);
        HighScoreTable.initRowHeight(restTableView);
        fetchDataAndPopulateTableViews();
        iniButtons();
    }

    public void setPlayerTime(String endTime) {
        this.endTime = endTime;
    }

    public void showGamePage(ActionEvent actionEvent) throws IOException {
        controller.playAgain();
        StageHandler.openStage(FilePath.EDGECLICKSCREEN.getFilePath());
    }

    public void showBonusPage(ActionEvent actionEvent) {

    }

    public void showHomeScreen(MouseEvent mouseEvent) throws IOException {
        controller.quitGame();
        StageHandler.openStage(FilePath.HOMEPAGE.getFilePath());
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

    private void iniButtons() {
        if (playerName != null && endTime != null) {
            btnBonus.setVisible(true);
            btnPlayAgain.setVisible(true);
            GamePageController.setPublicEndTime(null);
        }
    }

    private void fetchDataAndPopulateTableViews() {
        List<Player> playerList =
            TextFileEditor.readPlayerDataFromFile(FilePath.TEXT_FILE_PLAYER_PATH_LINUX.getFilePath());
        /*List<Player> playerList =
            TextFileEditor.readPlayerDataFromFile("src/main/resources/textfiles/highscore/player.txt");*/
        if (playerName != null && endTime != null) {
            Player currentPlayer = new Player(playerName, endTime);
            playerList.add(currentPlayer);
        }

        HighScoreTable.populateTableViews(playerList, tableView, restTableView);
        HighScoreTable.editTableViews(playerList, FilePath.WHOLE_TEXT_FILE_PLAYER_PATH_LINUX.getFilePath());
        //HighScoreTable.editTableViews(playerList, "src/main/resources/textfiles/highscore/player.txt");
    }
}
