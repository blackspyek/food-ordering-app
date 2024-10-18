package sample.test.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.test.model.User;
import sample.test.service.UserService;
import sample.test.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeViewController implements Initializable {

    @FXML
    private AnchorPane dashboardPane;
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane staffPane;
    @FXML
    private AnchorPane ordersPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button signOutButton;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button staffButton;
    @FXML
    private Button ordersButton;
    @FXML
    private Button userAddButton;
    @FXML
    private Button userEditButton;
    @FXML
    private Button userDeleteButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label usernameLabel2;
    @FXML
    private Label roleLabel2;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> rolesColumn;
    private Integer selectedUserId;

    public void initialize(URL url, ResourceBundle rb) {
        userNameLabel.setText(UserService.getInstance().getUsername());
        showDashboardPane();
        hideButtons();
        initUserTable();
        setUserPane(UserService.getInstance().getUsername(), UserService.getInstance().getUserRoles().toString());
    }

    public void setUserPane(String username, String roles) {
        usernameLabel2.setText(username);
        roleLabel2.setText(roles);
        if (Objects.equals(UserService.getInstance().getUsername(), username)) {
            userDeleteButton.setVisible(false);
            userEditButton.setVisible(false);
        } else {
            userDeleteButton.setVisible(true);
            userEditButton.setVisible(true);
        }

    }

    public void hideButtons() {
        if (!UserService.getInstance().getUserRoles().contains("ROLE_MANAGER")) {
            staffButton.setVisible(false);
            dashboardButton.setVisible(false);
        }
    }

    private void initUserTable() {
        setColumns();
        loadUserData();
        setTableListener();

    }

    private void setTableListener() {
        userTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                if (newValue != null) {
                    setUserPane(newValue.getUsername(), newValue.getRoles().toString());
                    selectedUserId = newValue.getId();
                }
            }
        });
    }

    private void setColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
    }

    private void loadUserData() {
        List<User> users = UserService.getInstance().getUsers();
        populateTableView(users);
    }

    private void populateTableView(List<User> users) {
        if (users != null) {
            ObservableList<User> userList = FXCollections.observableArrayList(users);
            userTableView.setItems(userList);
        } else {
            System.out.println("No users found or failed to load user data.");
        }
    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void switchPane(ActionEvent event) {
        if (event.getSource() == menuButton) {
            showMenuPane();
        } else if (event.getSource() == staffButton) {
            showStaffPane();
        } else if (event.getSource() == ordersButton) {
            showOrdersPane();
        } else {
            showDashboardPane();
        }
    }

    private void showMenuPane() {
        menuPane.setVisible(true);
        dashboardPane.setVisible(false);
        staffPane.setVisible(false);
        ordersPane.setVisible(false);
    }

    private void showStaffPane() {
        menuPane.setVisible(false);
        dashboardPane.setVisible(false);
        staffPane.setVisible(true);
        ordersPane.setVisible(false);
    }

    private void showOrdersPane() {
        menuPane.setVisible(false);
        dashboardPane.setVisible(false);
        staffPane.setVisible(false);
        ordersPane.setVisible(true);
    }

    private void showDashboardPane() {
        menuPane.setVisible(false);
        dashboardPane.setVisible(true);
        staffPane.setVisible(false);
        ordersPane.setVisible(false);
    }

    public void userAddButtonOnAction(ActionEvent event) {
        try {
            WindowUtils.loadView("register-view.fxml", "Register User", true, staffPane.getScene().getWindow(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userEditButtonOnAction(ActionEvent event) {
        if (selectedUserId != null) {
            try {
                WindowUtils.loadViewWithControllerAndData(
                        "edit-user-view.fxml",
                        "Edit User",
                        true,
                        staffPane.getScene().getWindow(),
                        true,
                        (EditUserViewController controller) -> {
                            controller.setUser(selectedUserId);
                        }
                );
                loadUserData();
                setUserPane(UserService.getInstance().getUsername(), UserService.getInstance().getUserRoles().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No user selected");
        }
    }

    public void userDeleteButtonOnAction(ActionEvent event) {
        if (selectedUserId != null) {
            boolean isDeleted = UserService.getInstance().deleteUser(selectedUserId);
            if (isDeleted) {
                loadUserData();
                setUserPane(UserService.getInstance().getUsername(), UserService.getInstance().getUserRoles().toString());
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("No user selected");
        }
    }

}









