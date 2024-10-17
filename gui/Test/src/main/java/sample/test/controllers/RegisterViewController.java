package sample.test.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import sample.test.dto.RegisterUserDto;
import sample.test.service.JwtTokenService;
import sample.test.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterViewController implements Initializable {

    @FXML
    private ImageView pencilImageView;
    @FXML
    private Button closeButton;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
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
    }

    public void registerButtonOnAction(ActionEvent event) {
        if (areFieldsValid()) {
            registerUser();
        }
    }

    private boolean areFieldsValid() {
        if (usernameTextField.getText().isBlank() || setPasswordField.getText().isBlank() || confirmPasswordField.getText().isBlank()) {
            registrationMessageLabel.setText("Please enter all fields.");
            return false;
        }
        if (!setPasswordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordLabel.setText("Password does not match.");
            return false;
        }
        return true;
    }

    public void registerUser() {
        try {
            RegisterUserDto registerUserDto = collectUserInput();
            String jwtToken = JwtTokenService.getInstance().getJwtToken();

            if (jwtToken != null && !jwtToken.isBlank()) {
                if (isUserManager()) {
                    HttpResponse<String> response = sendRegistrationRequest(registerUserDto, jwtToken);
                    handleRegistrationResponse(response);
                } else {
                    registrationMessageLabel.setText("Only managers can register a new user.");
                }
            } else {
                registrationMessageLabel.setText("You must be logged in to register a new user.");
            }
        } catch (Exception e) {
            registrationMessageLabel.setText("Error occurred during registration.");
        }
    }

    private boolean isUserManager() {
        List<String> userRoles = UserService.getInstance().getUserRoles();
        return userRoles != null && userRoles.contains("ROLE_MANAGER");
    }

    private RegisterUserDto collectUserInput() {
        String username = usernameTextField.getText();
        String password = setPasswordField.getText();
        return new RegisterUserDto(username, password);
    }

    private HttpResponse<String> sendRegistrationRequest(RegisterUserDto registerUserDto, String jwtToken) throws IOException, InterruptedException {
        Gson gson = new Gson();
        String jsonBody = gson.toJson(registerUserDto);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/signup"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwtToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void handleRegistrationResponse(HttpResponse<String> response) {
        if (isRegistrationSuccessful(response)) {
            registrationMessageLabel.setText("User registered successfully.");
        } else {
            handleRegistrationFailure(response);
        }
    }

    private boolean isRegistrationSuccessful(HttpResponse<String> response) {
        return response.statusCode() == 200;
    }

    private void handleRegistrationFailure(HttpResponse<String> response) {
        registrationMessageLabel.setText("Registration failed: " + response.body());
    }
}
