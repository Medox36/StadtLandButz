module ch.giuntini.stadtlandbutz_host {
    requires ch.giuntini.stadtlandbutz_package;
    requires javafx.controls;
    requires javafx.media;

    opens ch.giuntini.stadtlandbutz_host.gui to javafx.controls;
    exports ch.giuntini.stadtlandbutz_host.game;
    exports ch.giuntini.stadtlandbutz_host.net;
    exports ch.giuntini.stadtlandbutz_host.gui;
    exports ch.giuntini.stadtlandbutz_host;
}