module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    opens com.example.frontend.dto to com.fasterxml.jackson.databind;
    exports com.example.frontend.dto;

    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
}