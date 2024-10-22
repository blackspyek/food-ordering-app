package sample.test.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import sample.test.service.JwtTokenService;
import sample.test.service.UserService;
import sample.test.utils.HttpUtils;
import sample.test.utils.WindowUtils;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for the Login View in a JavaFX application.
 * Manages user login, authentication with a backend server, and handles navigation.
 * Upon successful login, the user's JWT token and roles are stored, and the view is redirected.
 * Implements Initializable to load images and initialize components.
 */

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

    /**
     * Initializes the Login View with branding and lock images.
     * @param url URL location of the FXML file to load.
     * @param rb  The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image brandingImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/logo.png")).toString());
        brandingImageView.setImage(brandingImage);

        Image lockImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        lockImageView.setImage(lockImage);

    }

    /**
     * Handles the login button action.
     * Validates the entered username and password, and sends a login request to the backend server.
     * Displays a message based on the response status code.
     * @param event The event that triggers the action.
     */
    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("You try to login");
            validateLogin();
        } else {
            loginMessageLabel.setText("Enter username and password.");
        }
    }

    /**
     * Handles the cancel button action.
     * Closes the current stage.
     * @param event The event that triggers the action.
     */
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates the entered username and password, and sends a login request to the backend server.
     * Displays a message based on the response status code.
     */
    public void validateLogin() {
        String username = usernameTextField.getText();
        String password = enterPasswordField.getText();
        String loginUrl = "http://localhost:8080/auth/login";

        try {
            String jsonInputString = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpResponse<String> response = HttpUtils.sendHttpRequest(loginUrl, "POST", null, jsonInputString);
            HttpUtils.checkIfResponseWasUnauthorized(response);
            handleLoginResponse(response);

        } catch (Exception e) {
            loginMessageLabel.setText("Error occurred. Check your connection.");
        }
    }

    /**
     * Handles the login response from the backend server.
     * Displays a message based on the response status code.
     * If the login is successful, stores the JWT token and roles, and redirects to the Employee View.
     * @param response The HTTP response from the backend server.
     */
    private void handleLoginResponse(HttpResponse<String> response) {
        if (isLoginSuccessful(response)) {
            handleSuccessfulLogin(response);
        } else if (isInvalidLogin(response)) {
            handleInvalidLogin();
        } else {
            handleUnexpectedError();
        }
    }

    /**
     * Checks if the login was successful based on the response status code.
     * @param response The HTTP response from the backend server.
     * @return True if the login was successful, false otherwise.
     */
    private boolean isLoginSuccessful(HttpResponse<String> response) {
        return response.statusCode() == 200;
    }

    /**
     * Checks if the login was invalid based on the response status code.
     * @param response The HTTP response from the backend server.
     * @return True if the login was invalid, false otherwise.
     */
    private boolean isInvalidLogin(HttpResponse<String> response) {
        return response.statusCode() == 400;
    }

    /**
     * Handles a successful login response from the backend server.
     * Extracts the JWT token and roles from the response, stores them, and redirects to the Employee View.
     * @param response The HTTP response from the backend server.
     */
    private void handleSuccessfulLogin(HttpResponse<String> response) {
        JsonObject responseObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject data = responseObject.getAsJsonObject("data");

        String jwtToken = data.get("token").getAsString();
        List<String> roles = extractRolesFromResponse(data.get("roles").getAsJsonArray());

        JwtTokenService.getInstance().setJwtToken(jwtToken);
        UserService.getInstance().setUsername(usernameTextField.getText());
        UserService.getInstance().setUserRoles(roles);

        loginMessageLabel.setText("Login successful! JWT token and roles stored.");

        try {
            WindowUtils.loadView("employee-view.fxml", "Employee View", false, null, false);
            closeCurrentStage();
        } catch (Exception e) {
            loginMessageLabel.setText("Error occurred while loading view.");
        }
    }

    /**
     * Extracts the roles from the response JSON array.
     * @param rolesArray The JSON array containing the roles.
     * @return A list of roles extracted from the JSON array.
     */
    private List<String> extractRolesFromResponse(JsonArray rolesArray) {
        List<String> roles = new ArrayList<>();
        rolesArray.forEach(roleElement -> roles.add(roleElement.getAsString()));
        return roles;
    }

    /**
     * Closes the current stage.
     */
    private void closeCurrentStage() {
        Stage loginStage = (Stage) loginMessageLabel.getScene().getWindow();
        loginStage.close();
    }

    /**
     * Handles an invalid login response from the backend server.
     * Displays a message indicating that the username or password is invalid.
     */
    private void handleInvalidLogin() {
        loginMessageLabel.setText("Invalid username or password.");
    }

    /**
     * Handles an unexpected error response from the backend server.
     * Displays a message indicating that an unexpected error occurred.
     */
    private void handleUnexpectedError() {
        loginMessageLabel.setText("Unexpected error. Try again.");
    }

}