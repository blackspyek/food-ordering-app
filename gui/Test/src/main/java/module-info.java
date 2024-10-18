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
    requires java.management;

    opens sample.test to javafx.fxml;
    exports sample.test;
    exports sample.test.controllers;
    exports sample.test.service;
    exports sample.test.model;
    exports sample.test.dto;
    exports sample.test.utils;
    exports sample.test.helpers;
    opens sample.test.controllers to javafx.fxml, com.google.gson;
    opens sample.test.dto to com.google.gson;
    opens sample.test.model to com.google.gson;
    opens sample.test.service to com.google.gson;
    opens sample.test.utils to com.google.gson;
    opens sample.test.helpers to com.google.gson;

}