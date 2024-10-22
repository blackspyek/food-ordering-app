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

    public void initialize(URL url, ResourceBundle rb) {
        setImage();
        initChoiceBox();
        initToggleButton();
    }

    private void setImage() {
        Image pencilImage = new Image(Objects.requireNonNull(getClass().getResource("/sample/test/images/padlock.png")).toString());
        pencilImageView.setImage(pencilImage);
    }

    public void setMenuItem(Long menuItemId) throws IOException {
        this.menuItemId = menuItemId;
        menuItem = MenuItemService.getMenuItemById(menuItemId);
        populateFields();
        setButtonText("Edit Item");
    }

    private void setButtonText(String text) {
        addItemButton.setText(text);
    }

    private void populateFields() {
        itemNameTextField.setText(menuItem.getName());
        itemDescriptionTextArea.setText(menuItem.getDescription());
        itemPriceTextField.setText(String.valueOf(menuItem.getPrice()));
        itemAvailableToggleButton.setSelected(menuItem.getAvailable());
        itemPhotoUrlTextField.setText(menuItem.getPhotoUrl());
        Category category = MenuItemService.convertCategory(menuItem.getCategory());
        itemCategoryChoiceBox.setValue(category);
    }

    private void initToggleButton() {
        itemAvailableToggleButton.setSelected(true);
        itemAvailableToggleButton.setText("Available");
    }

    private void initChoiceBox() {
        itemCategoryChoiceBox.getItems().addAll(Category.values());

    }

    public void setItemAvailableToggleButtonOnAction(ActionEvent event) {
        itemAvailableToggleButton.setText(itemAvailableToggleButton.isSelected() ? "Available" : "Unavailable");
    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void addItemButtonOnAction(ActionEvent event) throws IOException {
        if (areFieldsValid()) {
            if (menuItemId == null) {
                addMenuItem();
            } else {
                editMenuItem();
            }
        }
    }

    private void editMenuItem() throws IOException {
        MenuItemDto menuItemDto = createMenuItemDto();
        boolean success = MenuItemService.updateMenuItem(menuItemDto, menuItemId);
        displayResultMessage(success, "updated");
    }

    private void addMenuItem() throws IOException {
        MenuItemDto menuItemDto = createMenuItemDto();
        boolean success = MenuItemService.addMenuItem(menuItemDto);
        displayResultMessage(success, "added");
    }

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

    private void displayResultMessage(boolean success, String action) {
        resultLabel.setText(success ? "Menu item " + action + " successfully" : "Failed to " + action + " menu item");
    }

    private boolean areFieldsValid() {
        return isNameValid() && isPriceValid() && isPhotoUrlValid();
    }

    private boolean isPhotoUrlValid() {
        if (itemPhotoUrlTextField.getText().isEmpty()) {
            photoUrlErrorLabel.setText("Photo URL cannot be empty");
            return false;
        }
        photoUrlErrorLabel.setText("");
        return true;
    }

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

    private boolean isNameValid() {
        if (itemNameTextField.getText().isEmpty()) {
            nameErrorLabel.setText("Name cannot be empty");
            return false;
        }
        nameErrorLabel.setText("");
        return true;
    }

}
