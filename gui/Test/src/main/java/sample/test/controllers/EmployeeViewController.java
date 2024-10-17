package sample.test.controllers;

import com.google.gson.Gson;
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
import sample.test.service.JwtTokenService;
import sample.test.service.UserService;
import sample.test.utils.HttpUtils;
import sample.test.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.List;
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
    private Label userNameLabel;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> rolesColumn;

    public void initialize(URL url, ResourceBundle rb) {
        userNameLabel.setText(UserService.getInstance().getUsername());
        showDashboardPane();
        hideButtons();
        initUserTable();
    }

    public void hideButtons() {
        if (!UserService.getInstance().getUserRoles().contains("ROLE_MANAGER")) {
            staffButton.setVisible(false);
            dashboardButton.setVisible(false);
        }
    }

    private void initUserTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
        loadUserData();
    }

    private void loadUserData() {
        try {
            String url = "http://localhost:8080/users/";
            String token = JwtTokenService.getInstance().getJwtToken();

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "GET", token, null);
            String responseBody = response.body();
            List<User> users = parseJsonResponse(responseBody);
            populateTableView(users);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<User> parseJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        UserResponse userResponse = gson.fromJson(jsonResponse, UserResponse.class);
        return userResponse.getData();
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
    private static class UserResponse {
        private List<User> data;

        public List<User> getData() {
            return data;
        }

        public void setData(List<User> data) {
            this.data = data;
        }
    }

}









