package sample.test;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterViewController implements Initializable {

    @FXML
    private ImageView pencilImageView;
    @FXML
    private Button closeButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);

    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    public void registerButtonOnAction(ActionEvent event) {
        if (firstnameTextField.getText().isBlank() || lastnameTextField.getText().isBlank() || usernameTextField.getText().isBlank() || setPasswordField.getText().isBlank() || confirmPasswordField.getText().isBlank()) {
            registrationMessageLabel.setText("Please enter all fields.");
        }
        else if (setPasswordField.getText().equals(confirmPasswordField.getText())) {
            registerUser();
            confirmPasswordLabel.setText("");
            registrationMessageLabel.setText("User has been registered successfully.");
            loginForm();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();
        } else {
            registrationMessageLabel.setText("Password does not match");
        }
    }

    public void registerUser() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String username = usernameTextField.getText();
        String password = setPasswordField.getText();

        String insertFields = "INSERT INTO user_account(firstname, lastname, username, password) VALUES ('";
        String insertValues = firstname + "','" + lastname + "','" + username + "','" + password + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
            registrationMessageLabel.setText("User has been registered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

    }

    public void loginForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Stage registerStage = new Stage();
            //stage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 809, 539));
            registerStage.setTitle("Login Page");
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}
