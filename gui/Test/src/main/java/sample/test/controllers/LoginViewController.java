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
        Image brandingImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/logo.png")).toString());
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
            String jsonInputString = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpResponse<String> response = HttpUtils.sendHttpRequest(loginUrl, "POST", null, jsonInputString);
            HttpUtils.checkIfResponseWasUnauthorized(response);
            handleLoginResponse(response);

        } catch (Exception e) {
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

    private List<String> extractRolesFromResponse(JsonArray rolesArray) {
        List<String> roles = new ArrayList<>();
        rolesArray.forEach(roleElement -> roles.add(roleElement.getAsString()));
        return roles;
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

}