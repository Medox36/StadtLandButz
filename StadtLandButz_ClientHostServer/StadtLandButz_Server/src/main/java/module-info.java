module com.example.stadtlandbutz_server {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutz_server.gui to javafx.fxml;
    exports com.example.stadtlandbutz_server.gui;
    exports com.example.stadtlandbutz_server;
}