package ch.ladestation.connectncharge.pui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * plays sounds when user Interacts
 */
public final class Sounder {
    private static Media deactivateMedia;
    private static Media activateMedia;
    private static boolean initialized = false;

    private Sounder() {
    }

    public static void init() {
        String resourcePath = Sounder.class.getResource("/deactivate.mp3").toString();
        deactivateMedia = new Media(resourcePath);

        resourcePath = Sounder.class.getResource("/activate.mp3").toString();
        activateMedia = new Media(resourcePath);
        initialized = true;
    }

    public static void playActivate() {
        if (!initialized) {
            return;
        }
        var med = new MediaPlayer(activateMedia);
        med.setOnEndOfMedia(med::dispose);
        med.play();
    }

    public static void playDeactivate() {
        if (!initialized) {
            return;
        }
        var med = new MediaPlayer(deactivateMedia);
        med.setOnEndOfMedia(med::dispose);
        med.play();
    }
}
