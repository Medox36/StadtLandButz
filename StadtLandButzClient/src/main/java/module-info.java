module com.example.stadtlandbutzclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stadtlandbutzclient.gui to javafx.fxml;
    exports com.example.stadtlandbutzclient.gui;
}