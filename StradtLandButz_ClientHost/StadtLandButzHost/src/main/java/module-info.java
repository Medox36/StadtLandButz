module ch.giuntini.stadtlandbutzserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutzhost.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutzhost.gui;
    exports ch.giuntini.stadtlandbutzhost.game;
    exports ch.giuntini.stadtlandbutzhost.net;
    exports ch.giuntini.stadtlandbutzhost;
}