package sample.test.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.test.helpers.ColumnDefinition;
import sample.test.model.MenuItem;
import sample.test.model.User;
import sample.test.service.MenuItemService;
import sample.test.service.UserService;
import sample.test.utils.TableUtils;
import sample.test.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class EmployeeViewController implements Initializable {

    @FXML
    private AnchorPane dashboardPane, menuPane, staffPane, ordersPane, employeePane, dishPane;
    @FXML
    private Button closeButton, signOutButton, dashboardButton, menuButton, staffButton, ordersButton;
    @FXML
    private Button userEditButton, userDeleteButton, userAddButton;
    @FXML
    private Button menuItemEditButton, menuItemDeleteButton, menuItemAddButton;
    @FXML
    private ToggleButton availabilityToggleButton;
    @FXML
    private Label userNameLabel, usernameLabel, roleLabel, menuItemNameLabel, menuItemPriceLabel;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableView<MenuItem> menuTableView;

    private Integer selectedUserId;
    private Long selectedMenuItemId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userNameLabel.setText(UserService.getInstance().getUsername());
        hideButtonsIfNotManager();
        showPane(dashboardPane);

        initTable(userTableView, UserService.getInstance().getUsers(), this::setUserPane,
                Arrays.asList(new ColumnDefinition<>("ID", "id"),
                        new ColumnDefinition<>("Username", "username"),
                        new ColumnDefinition<>("Roles", "roles")));

        initTable(menuTableView, MenuItemService.getMenuItems(), this::setMenuItemPane,
                Arrays.asList(new ColumnDefinition<>("ID", "id"),
                        new ColumnDefinition<>("Name", "name"),
                        new ColumnDefinition<>("Price", "price")));


        employeePane.setVisible(false);
        dishPane.setVisible(false);
    }

    private void hideButtonsIfNotManager() {
        boolean isManager = UserService.getInstance().getUserRoles().contains("ROLE_MANAGER");
        staffButton.setVisible(isManager);
        dashboardButton.setVisible(isManager);
    }

    public void switchPane(ActionEvent event) {
        if (event.getSource() == menuButton) showPane(menuPane);
        else if (event.getSource() == staffButton) showPane(staffPane);
        else if (event.getSource() == ordersButton) showPane(ordersPane);
        else showPane(dashboardPane);
    }

    private void showPane(AnchorPane paneToShow) {
        List<AnchorPane> allPanes = Arrays.asList(dashboardPane, menuPane, staffPane, ordersPane);
        allPanes.forEach(pane -> pane.setVisible(pane == paneToShow));
    }

    private <T> void initTable(TableView<T> tableView, List<T> data, Consumer<T> consumer, List<ColumnDefinition<T, ?>> columnDefinitions) {
        setColumns(tableView, columnDefinitions);
        populateTable(tableView, data);
        setListener(tableView, consumer);
    }

    private static <T> void setListener(TableView<T> tableView, Consumer<T> consumer) {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                consumer.accept(newSelection);
            }
        });
    }

    private static <T> void populateTable(TableView<T> tableView, List<T> data) {
        ObservableList<T> observableList = FXCollections.observableArrayList(data);
        tableView.setItems(observableList);
    }

    private static <T> void setColumns(TableView<T> tableView, List<ColumnDefinition<T, ?>> columnDefinitions) {
        for (ColumnDefinition<T, ?> columnDefinition : columnDefinitions) {
            TableColumn<T, Object> tableColumn = new TableColumn<>(columnDefinition.getTitle());
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(columnDefinition.getProperty()));
            tableView.getColumns().add(tableColumn);
        }
    }

    private void setUserPane(User selectedUser) {
        if (selectedUser != null) {
            usernameLabel.setText(selectedUser.getUsername());
            roleLabel.setText(selectedUser.getRoles().toString());
            selectedUserId = selectedUser.getId();
            toggleUserEditDeleteButtons(!Objects.equals(UserService.getInstance().getUsername(), selectedUser.getUsername()));
        }
        employeePane.setVisible(true);
    }

    private void toggleUserEditDeleteButtons(boolean isVisible) {
        userEditButton.setVisible(isVisible);
        userDeleteButton.setVisible(isVisible);
    }

    private void setMenuItemPane(MenuItem selectedMenuItem) {
        if (selectedMenuItem != null) {
            menuItemNameLabel.setText(selectedMenuItem.getName());
            menuItemPriceLabel.setText(selectedMenuItem.getPrice().toString());
            availabilityToggleButton.setSelected(selectedMenuItem.getAvailable());
            selectedMenuItemId = selectedMenuItem.getId();
            setAvailabilityButtonText(selectedMenuItem.getAvailable());
        }
        dishPane.setVisible(true);
    }

    public void handleUserActions(ActionEvent event) throws IOException {
        if (event.getSource() == userDeleteButton) {
            deleteEntity(selectedUserId, UserService::deleteUser);
        } else if (event.getSource() == userEditButton) {
            loadEditForm("edit-user-view.fxml", selectedUserId);
        } else if (event.getSource() == userAddButton) {
            loadAddForm("register-view.fxml");
        }
    }

    public void handleMenuItemActions(ActionEvent event) throws IOException {
        if (event.getSource() == availabilityToggleButton) {
            toggleMenuItemAvailability();
        } else if (event.getSource() == menuItemDeleteButton) {
            deleteEntity(selectedMenuItemId, MenuItemService::deleteMenuItem);
        } else if (event.getSource() == menuItemEditButton) {
            loadEditForm("menu-item-form-view.fxml", selectedMenuItemId);
        } else if (event.getSource() == menuItemAddButton) {
            loadAddForm("menu-item-form-view.fxml");
        }
    }

    private <T> void loadEditForm(String fxml, T id) throws IOException {
        if (id != null) {
            loadViewAndPassData(fxml, id);
            refreshView();
        } else {
            System.out.println("No entity selected.");
        }
    }

    private <T> void loadViewAndPassData(String fxml, T id) throws IOException {
        WindowUtils.loadViewWithControllerAndData(fxml, "Edit " + (id instanceof Integer ? "User" : "Menu Item"), true,
                staffPane.getScene().getWindow(), true, controller -> {
                    if (controller instanceof EditUserViewController) {
                        ((EditUserViewController) controller).setUser((Integer) id);
                    } else if (controller instanceof MenuItemFormViewController) {
                        ((MenuItemFormViewController) controller).setMenuItem((Long) id);
                    }
                });
    }

    private void loadAddForm(String fxml) throws IOException {
        WindowUtils.loadView(fxml, "Add " + (fxml.contains("user") ? "User" : "Menu Item"), true,
                staffPane.getScene().getWindow(), true);
        refreshView();
    }

    private <T> void deleteEntity(T id, java.util.function.Predicate<T> deleteFunction) {
        if (id != null && deleteFunction.test(id)) {
            refreshView();
        } else {
            System.out.println("Failed to delete entity or no entity selected.");
        }
    }

    private void toggleMenuItemAvailability() {
        if (selectedMenuItemId != null) {
            boolean success =  MenuItemService.setMenuItemAvailability(selectedMenuItemId);
            if (success) System.out.println("Menu item availability updated successfully.");
            refreshView();
        } else {
            System.out.println("No menu item selected.");
        }
    }

    private void refreshView() {
        TableUtils.populateTableView(userTableView, UserService.getInstance().getUsers());
        TableUtils.populateTableView(menuTableView, MenuItemService.getMenuItems());
        employeePane.setVisible(false);
        dishPane.setVisible(false);
    }

    public void closeButtonOnAction(ActionEvent event) {
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private void setAvailabilityButtonText(boolean available) {
        availabilityToggleButton.setText(available ? "Available" : "Unavailable");
    }

}









