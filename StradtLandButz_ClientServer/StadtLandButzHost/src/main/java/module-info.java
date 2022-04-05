module com.example.stadtlandbutzserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutzhost.gui to javafx.fxml;
    exports com.example.stadtlandbutzhost.gui;
    exports com.example.stadtlandbutzhost.game;
    exports com.example.stadtlandbutzhost.net;
    exports com.example.stadtlandbutzhost;
}