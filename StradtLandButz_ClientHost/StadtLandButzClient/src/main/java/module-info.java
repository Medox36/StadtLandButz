module ch.giuntini.stadtlandbutzclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.giuntini.stadtlandbutzclient.gui to javafx.fxml;
    exports ch.giuntini.stadtlandbutzclient.gui;
    exports ch.giuntini.stadtlandbutzclient.game;
    exports ch.giuntini.stadtlandbutzclient.net;
    exports ch.giuntini.stadtlandbutzclient;
}