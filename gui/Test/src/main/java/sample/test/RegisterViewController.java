package sample.test;

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

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    private TextField usernameTextField;
    @FXML
    private ChoiceBox<String> roleChoiceBox;

    private String jwtToken;
    private String[] roles = {"ROLE_USER", "ROLE_MANAGER", "ROLE_EMPLOYEE"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);

        roleChoiceBox.getItems().addAll(roles);
        roleChoiceBox.setValue("ROLE_USER");
    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
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
        if (roleChoiceBox.getValue() == null || roleChoiceBox.getValue().isBlank()) {
            registrationMessageLabel.setText("Please select a role.");
            return false;
        }
        return true;
    }

    public void registerUser() {
        String username = usernameTextField.getText();
        String password = setPasswordField.getText();
        String role = roleChoiceBox.getValue();

        RegisterUserDto registerUserDto = new RegisterUserDto(username, password, role);
        try {
            Gson gson = new Gson();
            String jsonBody = gson.toJson(registerUserDto);

            String jwtToken = JwtTokenService.getInstance().getJwtToken();

            if (jwtToken != null && !jwtToken.isBlank()) {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/auth/signup"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + jwtToken)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                handleRegistrationResponse(response);
            } else {
                registrationMessageLabel.setText("You must be logged in to register a new user.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            registrationMessageLabel.setText("Error occurred during registration.");
        }
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
