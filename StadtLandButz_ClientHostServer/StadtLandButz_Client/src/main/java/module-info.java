module ch.giuntini.stadtlandbutz_client {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutz_client.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutz_client.gui;
    exports ch.giuntini.stadtlandbutz_client;
}