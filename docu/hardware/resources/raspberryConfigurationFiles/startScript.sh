#!/bin/bash
sudo killall -9 java
cd /home/pi && DISPLAY=:0 XAUTHORITY=/home/pi/.Xauthority sudo -E java --module-path /opt/javafx-sdk/lib:/home/pi/deploy --add-modules javafx.controls -Dglass.platform=gtk --module ch.ladestation.connectncharge/ch.ladestation.connectncharge.AppStarter
