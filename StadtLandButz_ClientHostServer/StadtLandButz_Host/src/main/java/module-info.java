module com.example.stadtlandbutz_host {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutz_host.gui to javafx.fxml;
    exports com.example.stadtlandbutz_host.gui;
    exports com.example.stadtlandbutz_host;
}