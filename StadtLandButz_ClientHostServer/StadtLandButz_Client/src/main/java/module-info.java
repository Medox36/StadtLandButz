module com.example.stadtlandbutz_client {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutz_client.gui to javafx.fxml;
    exports com.example.stadtlandbutz_client.gui;
    exports com.example.stadtlandbutz_client;
}