module com.firma {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    //requires java.sql;
    requires java.base;
    requires transitive java.sql;

    opens com.firma.controller to javafx.fxml;
    exports com.firma;
    exports com.firma.controller;
    exports com.firma.model;
    exports com.firma.util;
}