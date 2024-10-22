package com.example.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.android.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBackButtonHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    protected void configureActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (isCartAction(itemId)) {
            handleCartAction();
            return true;
        } else if (isBoardAction(itemId)) {
            handleBoardAction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isCartAction(int itemId) {
        return itemId == R.id.action_cart;
    }

    private boolean isBoardAction(int itemId) {
        return itemId == R.id.action_board;
    }

    private void handleCartAction() {
        if (!(this instanceof BasketActivity)) {
            openBasketActivity();
        }
    }

    private void handleBoardAction() {
        if (!(this instanceof BoardActivity)) {
            openBoardActivity();
        }
    }

    private void openBasketActivity() {
        Intent intent = new Intent(this, BasketActivity.class);
        startActivity(intent);
    }

    private void openBoardActivity() {
        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    void setupLogoClick() {
        ImageView logoImage = findViewById(R.id.logoImage);
        logoImage.setOnClickListener(v -> openMenuActivity());
    }

    private void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
