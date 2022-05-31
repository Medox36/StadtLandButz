module ch.giuntini.stadtlandbutz_host {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutz_host.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutz_host.game;
    exports ch.giuntini.stadtlandbutz_host.net;
    exports ch.giuntini.stadtlandbutz_host.gui;
    exports ch.giuntini.stadtlandbutz_host;
}