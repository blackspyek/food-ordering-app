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

public class EditUserViewController implements Initializable {

    @FXML
    private ImageView pencilImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private Button closeButton;
    @FXML
    private Button editButton;

    private Integer userId;
    private User user;
    private String[] roles = {"ROLE_MANAGER", "ROLE_EMPLOYEE"};


    public void initialize(URL url, ResourceBundle rb) {
        setImage();
        setRoles();
    }

    private void setRoles() {
        roleChoiceBox.getItems().addAll(roles);
    }

    private void setUserRole() {
        roleChoiceBox.setValue(user.getRoles().get(0));
    }

    private void setImage() {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);
    }

    public void setUser(Integer userId) {
        this.userId = userId;
        setLabels();
        setUserRole();
    }

    private void setLabels() {
        user = UserService.getInstance().getUserById(userId);
        usernameTextField.setText(user.getUsername());
    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void editButtonOnAction(ActionEvent event) {
        UpdateUserDto updateUserDto = new UpdateUserDto();

        updateUserDto.setUserName(usernameTextField.getText());
        Role roleEnum = Role.valueOf(roleChoiceBox.getValue());
        updateUserDto.setRoles(Collections.singleton(roleEnum));

        boolean success = UserService.getInstance().updateUser(userId, updateUserDto);
    }


}
