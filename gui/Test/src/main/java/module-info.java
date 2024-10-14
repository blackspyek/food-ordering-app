module sample.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mysql.connector.j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires fontawesomefx;
    requires java.desktop;
    requires java.net.http;
    requires com.google.gson;
    requires static lombok;

    opens sample.test to javafx.fxml;
    exports sample.test;
    exports sample.test.controllers;
    exports sample.test.service;
    opens sample.test.controllers to javafx.fxml;
    opens sample.test.dto to com.google.gson;

}