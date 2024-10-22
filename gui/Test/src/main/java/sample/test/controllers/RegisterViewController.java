package sample.test.controllers;

import com.google.gson.Gson;
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
import sample.test.utils.HttpUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class responsible for handling user registration in the application.
 * This class allows a logged-in user with the "ROLE_MANAGER" role to register new users by providing a username and password.
 * It handles the UI components for the registration form, performs input validation, and sends registration requests to the server.
 * <p>
 * The class implements the Initializable interface to set up the view components after the FXML has been loaded.
 * It also manages form submission, authorization checks, and displays appropriate success or error messages.
 */
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

    /**
     * Initializes the controller class.
     * This method is called after the FXML has been loaded and sets up the view components.
     * It initializes the image view with a padlock image.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb  The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);
    }

    /**
     * Closes the registration form window when the close button is clicked.
     *
     * @param event The action event that occurs when the close button is clicked.
     */
    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the registration button action by validating the input fields and registering a new user.
     * If the input fields are valid, the method collects the user input, sends a registration request to the server,
     * and displays the appropriate success or error message based on the response.
     *
     * @param event The action event that occurs when the registration button is clicked.
     */
    public void registerButtonOnAction(ActionEvent event) {
        if (areFieldsValid()) {
            registerUser();
        }
    }

    /**
     * Validates the input fields for user registration.
     * Checks if the username, password, and confirm password fields are not empty.
     * Checks if the password and confirm password fields match.
     * Displays error messages if the fields are invalid.
     *
     * @return true if the input fields are valid, false otherwise.
     */
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

    /**
     * Registers a new user by sending a registration request to the server.
     * The method collects the user input, retrieves the JWT token, and sends the registration request to the server.
     * It checks if the user is authorized to register a new user and handles the registration response accordingly.
     */
    public void registerUser() {
        try {
            RegisterUserDto registerUserDto = collectUserInput();
            String jwtToken = JwtTokenService.getInstance().getJwtToken();

            if (jwtToken != null && !jwtToken.isBlank()) {
                if (isUserManager()) {
                    HttpResponse<String> response = sendRegistrationRequest(registerUserDto, jwtToken);
                    HttpUtils.checkIfResponseWasUnauthorized(response);
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

    /**
     * Checks if the current user has the "ROLE_MANAGER" role.
     *
     * @return true if the user has the "ROLE_MANAGER" role, false otherwise.
     */
    private boolean isUserManager() {
        List<String> userRoles = UserService.getInstance().getUserRoles();
        return userRoles != null && userRoles.contains("ROLE_MANAGER");
    }

    /**
     * Collects the user input from the registration form fields.
     *
     * @return a RegisterUserDto object containing the username and password entered by the user.
     */
    private RegisterUserDto collectUserInput() {
        String username = usernameTextField.getText();
        String password = setPasswordField.getText();
        return new RegisterUserDto(username, password);
    }

    /**
     * Sends a registration request to the server with the user input and the JWT token.
     *
     * @param registerUserDto The RegisterUserDto object containing the user input (username and password).
     * @param jwtToken        The JWT token used for authorization.
     * @return the HttpResponse object containing the response from the server.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the operation is interrupted.
     */
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

    /**
     * Handles the registration response from the server.
     * Displays a success message if the registration was successful.
     * Displays an error message if the registration failed.
     *
     * @param response The HttpResponse object containing the response from the server.
     */
    private void handleRegistrationResponse(HttpResponse<String> response) {
        if (isRegistrationSuccessful(response)) {
            registrationMessageLabel.setText("User registered successfully.");
        } else {
            handleRegistrationFailure(response);
        }
    }

    /**
     * Checks if the registration was successful based on the response status code.
     *
     * @param response The HttpResponse object containing the response from the server.
     * @return true if the registration was successful (status code 200), false otherwise.
     */
    private boolean isRegistrationSuccessful(HttpResponse<String> response) {
        return response.statusCode() == 200;
    }

    /**
     * Handles the case when the registration fails by displaying an error message.
     *
     * @param response The HttpResponse object containing the response from the server.
     */
    private void handleRegistrationFailure(HttpResponse<String> response) {
        registrationMessageLabel.setText("Registration failed: " + response.body());
    }
}
