package ch.ladestation.connectncharge.model.game.gameinfo;

import ch.ladestation.connectncharge.services.file.TextFileEditor;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HighScoreTable {

    private static final int PLAYER_PLACE_TOP = 5;

    private HighScoreTable() {
        throw new AssertionError();
    }

    public static void initRowHeight(TableView<HighScorePlayer> tableView) {
        tableView.setRowFactory(param -> new TableRow<HighScorePlayer>() {
            @Override
            protected void updateItem(HighScorePlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    // Customize the row height here
                    setMinHeight(33); // Set the minimum height
                    setPrefHeight(33); // Set the preferred height
                    setMaxHeight(33); // Set the maximum height
                }
            }
        });
    }

    public static void initColumns(TableColumn<HighScorePlayer, Integer> rank,
                                   TableColumn<HighScorePlayer, String> name,
                                   TableColumn<HighScorePlayer, String> time) {
        rank.setCellValueFactory(cellData -> {
            int rankValue = cellData.getValue().getRank();
            return new SimpleIntegerProperty(rankValue).asObject();
        });
        name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayer().getPlayerName()));
        time.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayer().getEndTime()));
    }

    public static void populateTableViews(List<Player> playerList, TableView<HighScorePlayer> tableView,
                                          TableView<HighScorePlayer> restTableView) {
        // Sort the playerList based on the endTime (time)
        playerList.sort(Comparator.comparing(Player::getEndTime));
        List<HighScorePlayer> highScorePlayers = Stream.iterate(0, i -> i + 1).limit(playerList.size())
            .map(number -> new HighScorePlayer(number.intValue() + 1, playerList.get(number)))
            .collect(Collectors.toList());

        // Create an ObservableList from the player list
        ObservableList<HighScorePlayer> topPlayers;
        ObservableList<HighScorePlayer> restPlayers;

        if (highScorePlayers.size() > PLAYER_PLACE_TOP) {
            topPlayers = FXCollections.observableArrayList(highScorePlayers.subList(0, PLAYER_PLACE_TOP));
            restPlayers =
                FXCollections.observableArrayList(highScorePlayers.subList(PLAYER_PLACE_TOP, highScorePlayers.size()));
        } else {
            topPlayers = FXCollections.observableArrayList(highScorePlayers);
            restPlayers = FXCollections.emptyObservableList();
        }
        tableView.setItems(topPlayers);
        restTableView.setItems(restPlayers);
    }

    public static void editTableViews(List<Player> playerList, String filePath) {
        TextFileEditor.writeTextFile(filePath,
            playerList.stream()
                .map(val -> val.getPlayerName() + "," + val.getEndTime())
                .collect(Collectors.toList()));
    }
}
