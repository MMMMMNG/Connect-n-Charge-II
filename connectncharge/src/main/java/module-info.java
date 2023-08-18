open module ch.ladestation.connectncharge {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires org.slf4j;
    requires com.pi4j;
    requires com.pi4j.plugin.raspberrypi;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.library.pigpio;
    // Pi4J Modules
    requires com.pi4j.plugin.mock;
    requires com.pi4j.plugin.linuxfs;
}