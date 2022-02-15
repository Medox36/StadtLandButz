module com.example.stadtlandbutzserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutzserver.gui to javafx.fxml;
    exports com.example.stadtlandbutzserver.gui;
    exports com.example.stadtlandbutzserver.game;
    exports com.example.stadtlandbutzserver.net;
    exports com.example.stadtlandbutzserver;
}