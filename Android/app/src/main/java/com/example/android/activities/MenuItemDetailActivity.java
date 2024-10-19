package com.example.android.activities;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.managers.CartManager;
import com.example.android.models.MenuItem;

public class MenuItemDetailActivity extends BaseActivity {

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;
    private Button addToBasketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeMenuItemDetailsViews();
        deserializeMenuItem();
    }

    private void deserializeMenuItem() {
        MenuItem menuItem = (MenuItem) getIntent().getSerializableExtra("MENU_ITEM");
        if (menuItem != null) {
            addToBasketButton.setTag(menuItem);
            displayMenuItemDetails(menuItem);
        }
    }

    private void initializeMenuItemDetailsViews() {
        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        addToBasketButton = findViewById(R.id.addToBasketButton);

        handleAddToBasketButtonClick(addToBasketButton);
    }

    private void configureLayout() {
        setContentView(R.layout.activity_menu_item_detail);
    }

    private void displayMenuItemDetails(MenuItem menuItem) {
        displayMenuItemImage(menuItem, itemImage);
        setMenuItemNameAndDescription(menuItem, itemName, itemDescription);
    }

    private static void setMenuItemNameAndDescription(MenuItem menuItem, TextView itemName, TextView itemDescription) {
        itemName.setText(menuItem.getName());
        itemDescription.setText(menuItem.getDescription());
    }

    private void displayMenuItemImage(MenuItem menuItem, ImageView itemImage) {
        Glide.with(this)
                .load(menuItem.getPhotoUrl())
                .into(itemImage);
    }

    private static void handleAddToBasketButtonClick(Button addToBasketButton) {
        addToBasketButton.setOnClickListener(v -> {
            MenuItem menuItem = getMenuItemFromButton(v);
            if (menuItem != null) {
                addToCart(menuItem, v.getContext());
            }
        });
    }

    private static MenuItem getMenuItemFromButton(View view) {
        return (MenuItem) view.getTag();
    }

    private static void addToCart(MenuItem menuItem, Context context) {
        CartManager.getInstance().addToCart(menuItem);
        showAddToCartSuccessToast(context);
    }

    private static void showAddToCartSuccessToast(Context context) {
        Toast.makeText(context, R.string.successfullyAddedToCart, Toast.LENGTH_SHORT).show();
    }

}
