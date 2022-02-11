module com.example.stadtlandbutzserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutzserver to javafx.fxml;
    exports com.example.stadtlandbutzserver;
}