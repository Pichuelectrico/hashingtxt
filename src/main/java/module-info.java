module com.firma {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.base;
    requires transitive java.sql;
    requires org.apache.pdfbox;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;

    opens com.firma.controller to javafx.fxml;
    exports com.firma;
    exports com.firma.controller;
    exports com.firma.model;
    exports com.firma.util;
}