package com.example.android.activities;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.models.MenuItem;

public class MenuItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeAddToBasketButton();

        deserializeMenuItem();
    }

    private void deserializeMenuItem() {
        MenuItem menuItem = (MenuItem) getIntent().getSerializableExtra("MENU_ITEM");
        if (menuItem != null) {
            displayMenuItemDetails(menuItem);
        }
    }

    private void configureLayout() {
        setContentView(R.layout.activity_menu_item_detail);
    }

    private void setupBackButtonHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void configureActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void displayMenuItemDetails(MenuItem menuItem) {
        ImageView itemImage = findViewById(R.id.itemImage);
        TextView itemName = findViewById(R.id.itemName);
        TextView itemDescription = findViewById(R.id.itemDescription);

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

    private void initializeAddToBasketButton() {
        Button addToBasketButton = findViewById(R.id.addToBasketButton);
        addToBasketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MenuItemDetailActivity", "Add to basket clicked");
            }
        });
    }

}
