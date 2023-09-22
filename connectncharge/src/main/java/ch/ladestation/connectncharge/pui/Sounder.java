package ch.ladestation.connectncharge.pui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * plays sounds when user Interacts
 */
public final class Sounder {
    private static Media deactivateMedia;
    private static Media activateMedia;

    private static Media notificationMedia;

    private static Media winMedia;
    private static boolean initialized = false;

    private Sounder() {
    }

    public static void init() {
        String resourcePath = Sounder.class.getResource("/deactivate.mp3").toString();
        deactivateMedia = new Media(resourcePath);

        resourcePath = Sounder.class.getResource("/activate.mp3").toString();
        activateMedia = new Media(resourcePath);

        resourcePath = Sounder.class.getResource("/notification.mp3").toString();
        notificationMedia = new Media(resourcePath);

        resourcePath = Sounder.class.getResource("/success.mp3").toString();
        winMedia = new Media(resourcePath);
        initialized = true;
    }

    public static void playActivate() {
        playMedia(activateMedia);
    }

    public static void playDeactivate() {
        playMedia(deactivateMedia);
    }

    public static void playNotification() {
        playMedia(notificationMedia);
    }

    public static void playWin() {
        playMedia(winMedia);
    }

    private static void playMedia(Media media) {
        if (!initialized) {
            return;
        }
        var mp = new MediaPlayer(media);
        mp.setOnEndOfMedia(() -> {
            mp.dispose();
        });
        mp.setOnReady(() -> {
            mp.play();
        });
    }

    public static void shutdown() {
        //not needed i guess
    }
}
