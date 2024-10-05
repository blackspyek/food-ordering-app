package sample.test;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ImageView lockImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image brandingImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/_1c91bbc2-7932-4482-b967-78a06a1a5e12.jpeg")).toString());
        brandingImageView.setImage(brandingImage);

        Image lockImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        lockImageView.setImage(lockImage);

    }

    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("You try to login");
            validateLogin();
        } else {
            loginMessageLabel.setText("Enter username and password.");
        }
    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM user_account WHERE username = '" + usernameTextField.getText() + "' AND password = '" + enterPasswordField.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    loginMessageLabel.setText("Congratulations!");
                } else {
                    loginMessageLabel.setText("Invalid login. Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}