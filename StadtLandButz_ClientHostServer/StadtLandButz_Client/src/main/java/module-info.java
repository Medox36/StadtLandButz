module ch.giuntini.stadtlandbutz_client {
    requires ch.giuntini.stadtlandbutz_package;
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutz_client.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutz_client.game;
    exports ch.giuntini.stadtlandbutz_client.net;
    exports ch.giuntini.stadtlandbutz_client.gui;
    exports ch.giuntini.stadtlandbutz_client;
}