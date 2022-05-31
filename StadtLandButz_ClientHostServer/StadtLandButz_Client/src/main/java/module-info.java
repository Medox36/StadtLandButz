module ch.giuntini.stadtlandbutz_client {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutz_client.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutz_client.game;
    exports ch.giuntini.stadtlandbutz_client.net;
    exports ch.giuntini.stadtlandbutz_client.gui;
    exports ch.giuntini.stadtlandbutz_client;
}