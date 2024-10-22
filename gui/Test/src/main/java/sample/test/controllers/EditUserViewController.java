package sample.test.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sample.test.dto.UpdateUserDto;
import sample.test.model.Role;
import sample.test.model.User;
import sample.test.service.UserService;


import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for the Edit User view in the JavaFX application.
 * <p>
 * This class is responsible for handling user interactions related to editing user information,
 * such as updating the username and role of a user. It interacts with the view components
 * such as {@link TextField}, {@link ChoiceBox}, and {@link Button}.
 * </p>
 *
 * <p>
 * The class implements {@link Initializable} to allow initialization of the view components
 * when the view is loaded.
 * </p>
 */

public class EditUserViewController implements Initializable {

    @FXML
    private ImageView pencilImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private Button closeButton;

    private Integer userId;
    private User user;
    private final String[] roles = {"ROLE_MANAGER", "ROLE_EMPLOYEE"};

    /**
     * Initializes the controller class and sets up the view components.
     * <p>
     * This method is called automatically when the view is loaded.
     * It sets up the image for the pencil icon and populates the role choices.
     * </p>
     *
     * @param url The location used to resolve relative paths for the root object.
     * @param rb  The resources used to localize the root object.
     */
    public void initialize(URL url, ResourceBundle rb) {
        setImage();
        setRoles();
    }

    /**
     * Populates the {@link ChoiceBox} with available roles.
     */
    private void setRoles() {
        roleChoiceBox.getItems().addAll(roles);
    }

    /**
     * Sets the role of the user in the {@link ChoiceBox}.
     */
    private void setUserRole() {
        roleChoiceBox.setValue(user.getRoles().getFirst());
    }

    /**
     * Sets the image for the pencil icon.
     */
    private void setImage() {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);
    }

    /**
     * Sets the user to be edited.
     *
     * @param userId The ID of the user to be edited.
     */
    public void setUser(Integer userId) {
        this.userId = userId;
        setLabels();
        setUserRole();
    }

    /**
     * Sets the labels for the user to be edited.
     */
    private void setLabels() {
        user = UserService.getInstance().getUserById(userId);
        usernameTextField.setText(user.getUsername());
    }

    /**
     * Closes the Edit User view when the close button is clicked.
     *
     * @param event The event triggered when the close button is clicked.
     */
    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Updates the user information when the edit button is clicked.
     *
     * @param event The event triggered when the edit button is clicked.
     */
    public void editButtonOnAction(ActionEvent event) {
        UpdateUserDto updateUserDto = new UpdateUserDto();

        updateUserDto.setUserName(usernameTextField.getText());
        Role roleEnum = Role.valueOf(roleChoiceBox.getValue());
        updateUserDto.setRoles(Collections.singleton(roleEnum));

        boolean success = UserService.getInstance().updateUser(userId, updateUserDto);
    }


}
