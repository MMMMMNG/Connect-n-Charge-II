package ch.ladestation.connectncharge.model.text;

import java.io.File;

public enum FilePath {

    CREDITS("/ch/ladestation/connectncharge/credits.fxml"),
    LOADINGPAGE("/ch/ladestation/connectncharge/loadingpage.fxml"),
    HOMEPAGE("/ch/ladestation/connectncharge/homepage.fxml"),
    EDGECLICKSCREEN("/ch/ladestation/connectncharge/edgeclickscreen.fxml"),
    COUNTDOWNPAGE("/ch/ladestation/connectncharge/countdownpage.fxml"),
    HELPPAGE("/ch/ladestation/connectncharge/helppage.fxml"),
    GAMEPAGE("/ch/ladestation/connectncharge/gamepage.fxml"),
    ENDSCREEN("/ch/ladestation/connectncharge/endscreen.fxml"),
    NAMEINPUT("/ch/ladestation/connectncharge/nameinput.fxml"),
    HIGHSCORE("/ch/ladestation/connectncharge/highscore.fxml"),
    ADMINPAGE("/ch/ladestation/connectncharge/adminpage.fxml"),
    ADMINNORMALLEADERBOARD("/ch/ladestation/connectncharge/adminnormalleaderboard.fxml"),
    ADMINHOMEPAGE("/ch/ladestation/connectncharge/adminhomepage.fxml"),
    CSS("/css/style.css"),
    TEXT_FILE_PLAYER_PATH_LINUX("player.txt"),
    WHOLE_TEXT_FILE_PLAYER_PATH_LINUX(File.separator + "home" + File.separator + "pi" + File.separator + "player.txt");

    private final String filePath;

    FilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public FilePath getNext() {
        if (this.ordinal() == values().length - 1) {
            return values()[0];
        }
        return values()[this.ordinal() + 1];
    }

    public FilePath getPrevious() {
        if (this.ordinal() == 0) {
            return values()[values().length - 1];
        }
        return values()[this.ordinal() - 1];
    }
}
