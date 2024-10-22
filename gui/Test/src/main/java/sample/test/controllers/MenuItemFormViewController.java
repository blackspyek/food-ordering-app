package sample.test.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sample.test.dto.MenuItemDto;
import sample.test.model.Category;
import sample.test.model.MenuItem;
import sample.test.service.MenuItemService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class responsible for handling the logic of the Menu Item Form View.
 * This class allows users to create and edit menu items, including setting item details
 * like name, description, price, availability, category, and photo URL.
 * It also handles form validation and communication with the service layer to save or update the menu item.
 *
 * The class implements the Initializable interface to set up the view components after the FXML has been loaded.
 * It includes UI components such as text fields, toggle buttons, choice boxes, and labels for error handling.
 */
public class MenuItemFormViewController implements Initializable {
    @FXML
    private TextArea itemDescriptionTextArea;
    @FXML
    private TextField itemNameTextField, itemPriceTextField;
    @FXML
    private ToggleButton itemAvailableToggleButton;
    @FXML
    private ChoiceBox<Category> itemCategoryChoiceBox;
    @FXML
    private TextField itemPhotoUrlTextField;
    @FXML
    private Button closeButton, addItemButton;
    @FXML
    private ImageView pencilImageView;
    @FXML
    private Label nameErrorLabel, priceErrorLabel, photoUrlErrorLabel, resultLabel;

    private Long menuItemId;
    private MenuItem menuItem;

    /**
     * Initializes the controller class.
     * This method is called after the FXML has been loaded and sets up the view components.
     * It initializes the image view with a padlock image, choice box with category values, and toggle button.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb  The resources used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle rb) {
        setImage();
        initChoiceBox();
        initToggleButton();
    }

    /**
     * Sets the image view with a padlock image.
     */
    private void setImage() {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);
    }

    /**
     * Sets the menu item to be edited.
     * This method is called when the user wants to edit an existing menu item.
     * It sets the menu item ID and retrieves the menu item details from the service layer.
     * The method then populates the form fields with the menu item details and changes the button text to "Edit Item".
     *
     * @param menuItemId The ID of the menu item to be edited.
     * @throws IOException If an I/O error occurs when retrieving the menu item details.
     */
    public void setMenuItem(Long menuItemId) throws IOException {
        this.menuItemId = menuItemId;
        menuItem = MenuItemService.getMenuItemById(menuItemId);
        populateFields();
        setButtonText("Edit Item");
    }

    /**
     * Sets the button text.
     */
    private void setButtonText(String text) {
        addItemButton.setText(text);
    }

    /**
     * Populates the form fields with the menu item details.
     */
    private void populateFields() {
        itemNameTextField.setText(menuItem.getName());
        itemDescriptionTextArea.setText(menuItem.getDescription());
        itemPriceTextField.setText(String.valueOf(menuItem.getPrice()));
        itemAvailableToggleButton.setSelected(menuItem.getAvailable());
        itemPhotoUrlTextField.setText(menuItem.getPhotoUrl());
        Category category = MenuItemService.convertCategory(menuItem.getCategory());
        itemCategoryChoiceBox.setValue(category);
    }

    /**
     * Initializes the toggle button.
     */
    private void initToggleButton() {
        itemAvailableToggleButton.setSelected(true);
        itemAvailableToggleButton.setText("Available");
    }

    /**
     * Initializes the choice box with category values.
     */
    private void initChoiceBox() {
        itemCategoryChoiceBox.getItems().addAll(Category.values());

    }

    /**
     * Handles the action when the item available toggle button is clicked.
     * This method toggles the text of the toggle button between "Available" and "Unavailable".
     *
     * @param event The action event that occurs when the toggle button is clicked.
     */
    public void setItemAvailableToggleButtonOnAction(ActionEvent event) {
        itemAvailableToggleButton.setText(itemAvailableToggleButton.isSelected() ? "Available" : "Unavailable");
    }

    /**
     * Handles the action when the close button is clicked.
     * This method closes the current window when the close button is clicked.
     *
     * @param event The action event that occurs when the close button is clicked.
     */
    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action when the add item button is clicked.
     * This method validates the form fields and adds or updates the menu item based on the form data.
     * It displays a success message if the operation is successful or an error message if it fails.
     *
     * @param event The action event that occurs when the add item button is clicked.
     * @throws IOException If an I/O error occurs when adding or updating the menu item.
     */
    public void addItemButtonOnAction(ActionEvent event) throws IOException {
        if (areFieldsValid()) {
            if (menuItemId == null) {
                addMenuItem();
            } else {
                editMenuItem();
            }
        }
    }

    /**
     * Edits the menu item.
     *
     * @throws IOException If an I/O error occurs when editing the menu item.
     */
    private void editMenuItem() throws IOException {
        MenuItemDto menuItemDto = createMenuItemDto();
        boolean success = MenuItemService.updateMenuItem(menuItemDto, menuItemId);
        displayResultMessage(success, "updated");
    }

    /**
     * Adds the menu item.
     *
     * @throws IOException If an I/O error occurs when adding the menu item.
     */
    private void addMenuItem() throws IOException {
        MenuItemDto menuItemDto = createMenuItemDto();
        boolean success = MenuItemService.addMenuItem(menuItemDto);
        displayResultMessage(success, "added");
    }

    /**
     * Creates a menu item DTO from the form fields.
     *
     * @return The menu item DTO created from the form fields.
     */
    private MenuItemDto createMenuItemDto() {
        return new MenuItemDto(
                itemNameTextField.getText(),
                itemDescriptionTextArea.getText(),
                Double.parseDouble(itemPriceTextField.getText()),
                itemAvailableToggleButton.isSelected(),
                itemCategoryChoiceBox.getValue(),
                itemPhotoUrlTextField.getText()
        );
    }

    /**
     * Displays a result message based on the success of the operation.
     *
     * @param success The boolean value indicating the success of the operation.
     * @param action  The action performed on the menu item.
     */
    private void displayResultMessage(boolean success, String action) {
        resultLabel.setText(success ? "Menu item " + action + " successfully" : "Failed to " + action + " menu item");
    }

    /**
     * Validates the form fields.
     *
     * @return The boolean value indicating whether the form fields are valid.
     */
    private boolean areFieldsValid() {
        return isNameValid() && isPriceValid() && isPhotoUrlValid();
    }

    /**
     * Validates the photo URL field.
     *
     * @return The boolean value indicating whether the photo URL field is valid.
     */
    private boolean isPhotoUrlValid() {
        if (itemPhotoUrlTextField.getText().isEmpty()) {
            photoUrlErrorLabel.setText("Photo URL cannot be empty");
            return false;
        }
        photoUrlErrorLabel.setText("");
        return true;
    }

    /**
     * Validates the price field.
     *
     * @return The boolean value indicating whether the price field is valid.
     */
    private boolean isPriceValid() {
        try {
            double price = Double.parseDouble(itemPriceTextField.getText());
            if (price <= 0) {
                priceErrorLabel.setText("Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            priceErrorLabel.setText("Price must be a number");
            return false;
        }
        priceErrorLabel.setText("");
        return true;
    }

    /**
     * Validates the name field.
     *
     * @return The boolean value indicating whether the name field is valid.
     */
    private boolean isNameValid() {
        if (itemNameTextField.getText().isEmpty()) {
            nameErrorLabel.setText("Name cannot be empty");
            return false;
        }
        nameErrorLabel.setText("");
        return true;
    }

}
