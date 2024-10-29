package sample.test.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.test.dto.NotificationRequestDto;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static sample.test.utils.NotificationUtils.sendNotification;

/**
 * Controller class for managing the employee view in the JavaFX application.
 * <p>
 * This controller handles user interactions with the employee management, menu items, and orders sections.
 * It supports various functionalities such as switching views, editing user and menu item data,
 * handling order status changes, and downloading reports.
 * </p>
 */

public class EmployeeViewController implements Initializable {


    @FXML
    private Button dailyButton, weeklyButton, sendNotificationPaneButton;;
    @FXML
    private AnchorPane menuPane, staffPane, ordersPane, employeePane, dishPane, orderPane, navPane;
    @FXML
    private Button closeButton, menuButton, staffButton;
    @FXML
    private Button userEditButton, userDeleteButton, userAddButton;
    @FXML
    private Button deleteOrderButton, changeOrderStatusButton;
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

    @FXML
    private AnchorPane notificationPane;
    @FXML
    private TextField notificationTitleField;
    @FXML
    private TextField notificationBodyField;
    @FXML
    private TextField notificationImageField;
    @FXML
    private Label notificationResponseLabel;
    @FXML
    private Button sendNotificationButton;

    private Integer selectedUserId;
    private Long selectedMenuItemId;
    private ReportDownloader reportDownloader;
    private Long selectedOrderId;
    private ScheduledExecutorService scheduler;


    /**
     * Initializes the employee view by setting up the polling timeline, displaying the user name,
     * and setting the manager view if the user has manager roles.
     * It also hides buttons if the user does not have manager roles, initializes the menu and order tables,
     * and hides the dish and order panes.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPolling();
        userNameLabel.setText(UserService.getInstance().getUsername());
        showPane(ordersPane);
        setupValidationListeners();
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


    /**
     * Sets the manager view by initializing the user table with user data.
     */
    private void setManagerView() {
        TableUtils.initTable(userTableView, UserService.getInstance().getUsers(), this::setUserPane,
                Arrays.asList(new ColumnDefinition<>("ID", "id"),
                        new ColumnDefinition<>("Username", "username"),
                        new ColumnDefinition<>("Roles", "roles")), null);


    }

    /**
     * Sets up the polling timeline to refresh the orders table every 2 seconds.
     */
    private void setupPolling() {
        scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                var orders = OrderService.getOrders();

                Platform.runLater(() -> refreshView(ordersTableView, orderPane, orders));
            } catch (IOException e) {
                Platform.runLater(() -> {
                    throw new RuntimeException(e);
                });
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Hides the buttons if the user does not have manager roles.
     */
    private void hideButtonsIfNotManager() {
        boolean isManager = UserService.getInstance().getUserRoles().contains("ROLE_MANAGER");
        staffButton.setVisible(isManager);
        dailyButton.setVisible(isManager);
        weeklyButton.setVisible(isManager);
        menuItemAddButton.setVisible(isManager);
        menuItemDeleteButton.setVisible(isManager);
        menuItemEditButton.setVisible(isManager);
        sendNotificationPaneButton.setVisible(isManager);
    }

    /**
     * Switches the view to the menu, staff, or orders pane based on the button clicked.
     */
    public void switchPane(ActionEvent event) {
        if (event.getSource() == menuButton) showPane(menuPane);
        else if (event.getSource() == staffButton) showPane(staffPane);
        else showPane(ordersPane);
    }

    /**
     * Shows the pane based on the pane to show.
     */
    private void showPane(AnchorPane paneToShow) {
        List<AnchorPane> allPanes = Arrays.asList(menuPane, staffPane, ordersPane, notificationPane);
        allPanes.forEach(pane -> pane.setVisible(pane == paneToShow));
    }

    /**
     * Sets the user pane with the selected user data.
     */
    private void setUserPane(User selectedUser) {
        if (selectedUser != null) {
            usernameLabel.setText(selectedUser.getUsername());
            roleLabel.setText(selectedUser.getRoles().toString());
            selectedUserId = selectedUser.getId();
            toggleUserEditDeleteButtons(!Objects.equals(UserService.getInstance().getUsername(), selectedUser.getUsername()));
        }
        employeePane.setVisible(true);
    }

    /**
     * Toggles the user edit and delete buttons based on the visibility.
     */
    private void toggleUserEditDeleteButtons(boolean isVisible) {
        userEditButton.setVisible(isVisible);
        userDeleteButton.setVisible(isVisible);
    }

    /**
     * Sets the menu item pane with the selected menu item data.
     */
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

    /**
     * Sets the order pane with the selected order data.
     */
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
        } else {
            orderBoardCodeTextField.setText("");
            orderTypeTextField.setText("");
            totalAmountTextField.setText("");
            orderStatusChoiceBox.setValue(null);
            orderItemsTableView.getItems().clear();
        }
        orderPane.setVisible(true);
    }

    /**
     * Handles the user actions based on the button clicked.
     */
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

    /**
     * Handles the menu item actions based on the button clicked.
     */
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

    /**
     * Handles the menu item actions based on the button clicked.
     */
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

    /**
     * Handles the order actions based on the button clicked.
     */
    public void handleOrderActions(ActionEvent event) throws IOException {
        if (event.getSource() == deleteOrderButton) {
            deleteEntity(selectedOrderId, OrderService::deleteOrder, ordersTableView, orderPane, OrderService.getOrders());
            refreshView(ordersTableView, orderPane, OrderService.getOrders());
            selectedOrderId = null;
            setOrderPane(null);
        } else if (event.getSource() == changeOrderStatusButton) {
            changeOrderStatusButtonOnAction(event);
        }
    }

    /**
     * Downloads the report based on the button clicked.
     */
    public void downloadReport(ActionEvent event) {
        reportDownloader.downloadReport(event);
    }
    /**
     * Displays the notification pane.
     */

    @FXML
    public void sendNotif(ActionEvent event) {
        showPane(notificationPane);
    }

    /**
     * Handles the send notification action based on the button clicked.
     */

    @FXML
    public void handleSendNotification(ActionEvent event) {
        if (!validateNotificationFields()) {
            return;
        }
        String title = notificationTitleField.getText();
        String body = notificationBodyField.getText();
        String image = notificationImageField.getText();

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto(title, body, image);
        try {
            String response = sendNotification("News", notificationRequestDto);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Notification sent successfully:\n" + response);

            notificationTitleField.clear();
            notificationBodyField.clear();
            notificationImageField.clear();

            Timeline timeline = new Timeline(
                    new KeyFrame(
                            javafx.util.Duration.seconds(5),
                            ae -> notificationResponseLabel.setText("")
                    )
            );
            timeline.play();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send notification:\n" + e.getMessage());
        }


    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private boolean validateNotificationFields() {
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        // Check for empty fields
        if (notificationTitleField.getText().trim().isEmpty()) {
            errorMessage.append("Title cannot be empty\n");
            notificationTitleField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            notificationTitleField.setStyle("");
        }

        if (notificationBodyField.getText().trim().isEmpty()) {
            errorMessage.append("Body cannot be empty\n");
            notificationBodyField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            notificationBodyField.setStyle("");
        }

        // Check if image URL is not empty and starts with http:// or https://
        String imageUrl = notificationImageField.getText().trim();
        if (!imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                errorMessage.append("Image URL must start with http:// or https://\n");
                notificationImageField.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                notificationImageField.setStyle("");
            }
        } else {
            errorMessage.append("Image URL cannot be empty\n");
            notificationImageField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!isValid) {
            notificationResponseLabel.setStyle("-fx-text-fill: red;");
            notificationResponseLabel.setText(errorMessage.toString());
        }

        return isValid;
    }

    // Add these methods to clear validation styling
    private void clearValidationStyles() {
        notificationTitleField.setStyle("");
        notificationBodyField.setStyle("");
        notificationImageField.setStyle("");
        notificationResponseLabel.setText("");
    }

    // Add these field listeners in your initialize method
    private void setupValidationListeners() {
        // Add listeners to clear red border when user starts typing
        notificationTitleField.textProperty().addListener((observable, oldValue, newValue) -> {
            notificationTitleField.setStyle("");
            notificationResponseLabel.setText("");
        });

        notificationBodyField.textProperty().addListener((observable, oldValue, newValue) -> {
            notificationBodyField.setStyle("");
            notificationResponseLabel.setText("");
        });

        notificationImageField.textProperty().addListener((observable, oldValue, newValue) -> {
            notificationImageField.setStyle("");
            notificationResponseLabel.setText("");
        });
    }
    /**
     * Handles the daily and weekly report actions based on the button clicked.
     */
    private <T, V> void loadEditForm(String fxml, T id, TableView<V> tableView, AnchorPane pane, List<V> items) throws IOException {
        if (id != null) {
            loadViewAndPassData(fxml, id);
            refreshView(tableView, pane, items);
        } else {
            System.out.println("No entity selected.");
        }
    }

    /**
     * Loads the view and passes the data based on the fxml and id.
     */
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

    /**
     * Loads the add form based on the fxml, table view, pane, and items.
     */
    private <T> void loadAddForm(String fxml, TableView<T> tableView, AnchorPane pane, List<T> items) throws IOException {
        WindowUtils.loadView(fxml, "Add " + (fxml.contains("user") ? "User" : "Menu Item"), true,
                staffPane.getScene().getWindow(), true);
        refreshView(tableView, pane, items);
    }

    /**
     * Deletes the entity based on the id, delete function, table view, pane, and items.
     */
    private <T, V> void deleteEntity(T id, PredicateWithIOException<T> deleteFunction, TableView<V> tableView, AnchorPane pane, List<V> items) throws IOException {
        if (id != null && deleteFunction.test(id)) {
            refreshView(tableView, pane, items);
        } else {
            System.out.println("Failed to delete entity or no entity selected.");
        }
    }

    /**
     * Sets the menu item availability based on the selected menu item id.
     */
    private void toggleMenuItemAvailability() throws IOException {
        if (selectedMenuItemId != null) {
            boolean success =  MenuItemService.setMenuItemAvailability(selectedMenuItemId);
            if (success) System.out.println("Menu item availability updated successfully.");
            refreshView(menuTableView, dishPane, MenuItemService.getMenuItems());
        } else {
            System.out.println("No menu item selected.");
        }
    }

    /**
     * Refreshes the view based on the table view, pane, and items.
     */
    private <T> void refreshView(TableView<T> tableView, AnchorPane pane, List<T> items) {
        TableUtils.populateTable(tableView, items);
        pane.setVisible(true);
    }
    /**
     * Stops polling when it's no longer needed.
     */
    private void stopPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Handles the daily and weekly report actions based on the button clicked.
     */
    public void closeButtonOnAction() {
        stopPolling();
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    /**
     * Handles the daily and weekly report actions based on the button clicked.
     */
    private void setAvailabilityButtonText(boolean available) {
        availabilityToggleButton.setText(available ? "Available" : "Unavailable");
    }

    /**
     * Handles the daily and weekly report actions based on the button clicked.
     */
    public void signOutButtonOnAction() throws IOException {
        HttpUtils.reloadApp();
    }

}









