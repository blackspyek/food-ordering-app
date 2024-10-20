package com.example.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupToolbar();
        configureActionBar();

        LinearLayout eatInButton = findViewById(R.id.eatInButton);
        LinearLayout takeOutButton = findViewById(R.id.takeOutButton);

        eatInButton.setOnClickListener(view -> {
            saveChoice("DINE_IN");
            navigateToMenu();
        });

        takeOutButton.setOnClickListener(view -> {
            saveChoice("TAKE_OUT");
            navigateToMenu();
        });
    }

    private void saveChoice(String choice) {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("dining_option", choice);
        editor.apply();
    }

    private void navigateToMenu() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
//          Intent intent = new Intent(MainActivity.this, OrderBoardActivity.class);
//          startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem boardItem = menu.findItem(R.id.action_cart);
        if (boardItem != null) {
            boardItem.setVisible(false);
        }

        return true;
    }
    @Override
    protected void configureActionBar() {
        super.configureActionBar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}