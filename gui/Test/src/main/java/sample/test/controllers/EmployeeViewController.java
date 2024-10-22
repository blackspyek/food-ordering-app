package sample.test.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.test.dto.UpdateOrderStatusDto;
import sample.test.helpers.ColumnDefinition;
import sample.test.model.*;
import sample.test.model.MenuItem;
import sample.test.service.MenuItemService;
import sample.test.service.OrderService;
import sample.test.service.UserService;
import sample.test.utils.HttpUtils;
import sample.test.utils.ReportDownloader;
import sample.test.utils.TableUtils;
import sample.test.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeViewController implements Initializable {

    @FXML
    private Button dailyButton, weeklyButton;
    @FXML
    private AnchorPane menuPane, staffPane, ordersPane, employeePane, dishPane, orderPane, navPane;
    @FXML
    private Button closeButton, menuButton, staffButton;
    @FXML
    private Button userEditButton, userDeleteButton, userAddButton;
    @FXML
    private Button menuItemEditButton, menuItemDeleteButton, menuItemAddButton;
    @FXML
    private ChoiceBox<OrderStatus> orderStatusChoiceBox;
    @FXML
    private ToggleButton availabilityToggleButton;
    @FXML
    private Label userNameLabel, usernameLabel, roleLabel;
    @FXML
    private Label menuItemNameLabel, menuItemPriceLabel;
    @FXML
    private TextField orderBoardCodeTextField, totalAmountTextField, orderTypeTextField;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableView<MenuItem> menuTableView;
    @FXML
    private TableView<Order> ordersTableView;
    @FXML
    private TableView<OrderItem> orderItemsTableView;

    private Integer selectedUserId;
    private Long selectedMenuItemId;
    private ReportDownloader reportDownloader;
    private Long selectedOrderId;
    private Timeline pollingTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPolling();
        userNameLabel.setText(UserService.getInstance().getUsername());
        showPane(ordersPane);
        reportDownloader = new ReportDownloader(navPane);

        if (UserService.getInstance().getUserRoles().contains("ROLE_MANAGER")) {
            setManagerView();
            employeePane.setVisible(false);
        } else {
            hideButtonsIfNotManager();
        }
        try {
            TableUtils.initTable(menuTableView, MenuItemService.getMenuItems(), this::setMenuItemPane,
                    Arrays.asList(new ColumnDefinition<>("ID", "id"),
                            new ColumnDefinition<>("Name", "name"),
                            new ColumnDefinition<>("Price", "price")), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            TableUtils.initTable(ordersTableView, OrderService.getOrders(), this::setOrderPane,
                    Arrays.asList(new ColumnDefinition<>("ID", "orderId"),
                            new ColumnDefinition<>("Board Code", "boardCode"),
                            new ColumnDefinition<>("Order Time", "orderTime"),
                            new ColumnDefinition<>("Status", "status")), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dishPane.setVisible(false);
        orderPane.setVisible(false);
    }

    private void setManagerView() {
        TableUtils.initTable(userTableView, UserService.getInstance().getUsers(), this::setUserPane,
                Arrays.asList(new ColumnDefinition<>("ID", "id"),
                        new ColumnDefinition<>("Username", "username"),
                        new ColumnDefinition<>("Roles", "roles")), null);


    }

    private void setupPolling() {
        pollingTimeline = new Timeline(
                new KeyFrame(Duration.millis(2000), _ -> {
                    try {
                        refreshView(ordersTableView, orderPane, OrderService.getOrders());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    private void hideButtonsIfNotManager() {
        boolean isManager = UserService.getInstance().getUserRoles().contains("ROLE_MANAGER");
        staffButton.setVisible(isManager);
        dailyButton.setVisible(isManager);
        weeklyButton.setVisible(isManager);
        menuItemAddButton.setVisible(isManager);
        menuItemDeleteButton.setVisible(isManager);
        menuItemEditButton.setVisible(isManager);

    }

    public void switchPane(ActionEvent event) {
        if (event.getSource() == menuButton) showPane(menuPane);
        else if (event.getSource() == staffButton) showPane(staffPane);
        else showPane(ordersPane);
    }

    private void showPane(AnchorPane paneToShow) {
        List<AnchorPane> allPanes = Arrays.asList(menuPane, staffPane, ordersPane);
        allPanes.forEach(pane -> pane.setVisible(pane == paneToShow));
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

    private void setOrderPane(Order selectedOrder) {
        if (selectedOrder != null) {
            orderBoardCodeTextField.setText(selectedOrder.getBoardCode());
            orderTypeTextField.setText(selectedOrder.getOrderType());
            totalAmountTextField.setText(selectedOrder.getTotalPrice().toString());
            orderStatusChoiceBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));
            orderStatusChoiceBox.setValue(OrderService.convertOrderStatus(selectedOrder.getStatus()));
            selectedOrderId = selectedOrder.getOrderId();

            orderItemsTableView.getColumns().clear();

            TableUtils.initTable(orderItemsTableView, OrderService.getOrderItems(selectedOrderId), null,
                    Arrays.asList(new ColumnDefinition<>("Item", "item.name"),
                            new ColumnDefinition<>("Quantity", "quantity"),
                            new ColumnDefinition<>("Total Price", "totalPrice")),
                    (tableView, _) -> {
                        TableColumn<OrderItem, String> itemNameColumn = (TableColumn<OrderItem, String>) tableView.getColumns().getFirst();
                        itemNameColumn.setCellValueFactory(cellData -> {
                            MenuItem menuItem = cellData.getValue().getItem();
                            return new SimpleStringProperty(menuItem != null ? menuItem.getName() : "Unknown Item");
                        });
                    });
        }
        orderPane.setVisible(true);
    }

    public void handleUserActions(ActionEvent event) throws IOException {
        if (event.getSource() == userDeleteButton) {
            deleteEntity(selectedUserId, UserService::deleteUser, userTableView, employeePane, UserService.getInstance().getUsers());
            refreshView(userTableView, employeePane, UserService.getInstance().getUsers());
            employeePane.setVisible(false);
        } else if (event.getSource() == userEditButton) {
            loadEditForm("edit-user-view.fxml", selectedUserId, userTableView, employeePane, UserService.getInstance().getUsers());
            refreshView(userTableView, employeePane, UserService.getInstance().getUsers());
            employeePane.setVisible(false);
        } else if (event.getSource() == userAddButton) {
            loadAddForm("register-view.fxml", userTableView, employeePane, UserService.getInstance().getUsers());
            refreshView(userTableView, employeePane, UserService.getInstance().getUsers());
        }
    }

    public void changeOrderStatusButtonOnAction(ActionEvent event) throws IOException {
        if (orderStatusChoiceBox.getValue() != null) {
            OrderStatus newStatus = orderStatusChoiceBox.getValue();
            UpdateOrderStatusDto updateOrderStatusDto = new UpdateOrderStatusDto(OrderService.convertOrderStatus(newStatus.toString()));
            boolean success = OrderService.changeOrderStatus(selectedOrderId, updateOrderStatusDto);
            if (success) System.out.println("Order status updated successfully.");
            refreshView(ordersTableView, orderPane, OrderService.getOrders());
            orderPane.setVisible(false);
        } else {
            System.out.println("No order status selected.");
        }
    }

    public void handleMenuItemActions(ActionEvent event) throws IOException {
        if (event.getSource() == availabilityToggleButton) {
            toggleMenuItemAvailability();
            dishPane.setVisible(false);
        } else if (event.getSource() == menuItemDeleteButton) {
            deleteEntity(selectedMenuItemId, MenuItemService::deleteMenuItem, menuTableView, dishPane, MenuItemService.getMenuItems());
            refreshView(menuTableView, dishPane, MenuItemService.getMenuItems());
            dishPane.setVisible(false);
        } else if (event.getSource() == menuItemEditButton) {
            loadEditForm("menu-item-form-view.fxml", selectedMenuItemId, menuTableView, dishPane, MenuItemService.getMenuItems());
            refreshView(menuTableView, dishPane, MenuItemService.getMenuItems());
            dishPane.setVisible(false);
        } else if (event.getSource() == menuItemAddButton) {
            loadAddForm("menu-item-form-view.fxml", menuTableView, dishPane, MenuItemService.getMenuItems());
            refreshView(menuTableView, dishPane, MenuItemService.getMenuItems());
        }

    }

    public void downloadReport(ActionEvent event) {
        reportDownloader.downloadReport(event);
    }


    private <T, V> void loadEditForm(String fxml, T id, TableView<V> tableView, AnchorPane pane, List<V> items) throws IOException {
        if (id != null) {
            loadViewAndPassData(fxml, id);
            refreshView(tableView, pane, items);
        } else {
            System.out.println("No entity selected.");
        }
    }

    private <T> void loadViewAndPassData(String fxml, T id) throws IOException {
        WindowUtils.loadViewWithControllerAndData(fxml, "Edit " + (id instanceof Integer ? "User" : "Menu Item"), true,
                staffPane.getScene().getWindow(), true, controller -> {
                    if (controller instanceof EditUserViewController) {
                        assert id instanceof Integer;
                        ((EditUserViewController) controller).setUser((Integer) id);
                    } else if (controller instanceof MenuItemFormViewController) {
                        assert id instanceof Long;
                        try {
                            ((MenuItemFormViewController) controller).setMenuItem((Long) id);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    private <T> void loadAddForm(String fxml, TableView<T> tableView, AnchorPane pane, List<T> items) throws IOException {
        WindowUtils.loadView(fxml, "Add " + (fxml.contains("user") ? "User" : "Menu Item"), true,
                staffPane.getScene().getWindow(), true);
        refreshView(tableView, pane, items);
    }

    private <T, V> void deleteEntity(T id, PredicateWithIOException<T> deleteFunction, TableView<V> tableView, AnchorPane pane, List<V> items) throws IOException {
        if (id != null && deleteFunction.test(id)) {
            refreshView(tableView, pane, items);
        } else {
            System.out.println("Failed to delete entity or no entity selected.");
        }
    }

    private void toggleMenuItemAvailability() throws IOException {
        if (selectedMenuItemId != null) {
            boolean success =  MenuItemService.setMenuItemAvailability(selectedMenuItemId);
            if (success) System.out.println("Menu item availability updated successfully.");
            refreshView(menuTableView, dishPane, MenuItemService.getMenuItems());
        } else {
            System.out.println("No menu item selected.");
        }
    }

    private <T> void refreshView(TableView<T> tableView, AnchorPane pane, List<T> items) {
        TableUtils.populateTable(tableView, items);
        pane.setVisible(true);
    }

    public void closeButtonOnAction() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private void setAvailabilityButtonText(boolean available) {
        availabilityToggleButton.setText(available ? "Available" : "Unavailable");
    }

    public void signOutButtonOnAction() throws IOException {
        HttpUtils.reloadApp();
    }

}









