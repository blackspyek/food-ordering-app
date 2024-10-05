package sample.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 809, 539);
        Scene scene = new Scene(fxmlLoader.load(), 809, 579);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Register Page");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}