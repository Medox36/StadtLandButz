module ch.giuntini.stadtlandbutz_server {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutz_server.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutz_server.gui;
    exports ch.giuntini.stadtlandbutz_server;
}