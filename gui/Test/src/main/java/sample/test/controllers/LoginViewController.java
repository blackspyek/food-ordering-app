package sample.test.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import sample.test.Main;
import sample.test.service.JwtTokenService;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    private String jwtToken;

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
        String username = usernameTextField.getText();
        String password = enterPasswordField.getText();
        String loginUrl = "http://localhost:8080/auth/login";

        try {
            HttpClient client = HttpClient.newHttpClient();

            String jsonInputString = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            handleLoginResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Error occurred. Check your connection.");
        }
    }

    private void handleLoginResponse(HttpResponse<String> response) {
        if (isLoginSuccessful(response)) {
            handleSuccessfulLogin(response);
        } else if (isInvalidLogin(response)) {
            handleInvalidLogin();
        } else {
            handleUnexpectedError();
        }
    }

    private boolean isLoginSuccessful(HttpResponse<String> response) {
        return response.statusCode() == 200;
    }

    private boolean isInvalidLogin(HttpResponse<String> response) {
        return response.statusCode() == 400;
    }

    private void handleSuccessfulLogin(HttpResponse<String> response) {
        String jwtToken = extractTokenFromResponse(response.body());
        JwtTokenService.getInstance().setJwtToken(jwtToken);
        loginMessageLabel.setText("Login successful! JWT token stored.");

        try {
            loadRegisterView();
            closeCurrentStage();
        } catch (Exception e) {
            loginMessageLabel.setText("Error occurred while loading register view.");
        }

    }

    private void loadRegisterView() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("register-view.fxml"));
        Parent registerViewRoot = loader.load();

        Stage registerStage = new Stage();
        registerStage.setTitle("Register Page");
        registerStage.setScene(new Scene(registerViewRoot));
        registerStage.show();
    }

    private void closeCurrentStage() {
        Stage loginStage = (Stage) loginMessageLabel.getScene().getWindow();
        loginStage.close();
    }

    private void handleInvalidLogin() {
        loginMessageLabel.setText("Invalid username or password.");
    }

    private void handleUnexpectedError() {
        loginMessageLabel.setText("Unexpected error. Try again.");
    }

    private String extractTokenFromResponse(String responseBody) {
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return jsonObject.get("token").getAsString();
    }

}