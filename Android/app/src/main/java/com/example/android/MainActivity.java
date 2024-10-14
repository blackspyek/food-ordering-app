package com.example.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        LinearLayout eatInButton = findViewById(R.id.eatInButton);
        LinearLayout takeOutButton = findViewById(R.id.takeOutButton);

        eatInButton.setOnClickListener(view -> {
            saveChoice("Eat In");
            navigateToMenu();
        });

        takeOutButton.setOnClickListener(view -> {
            saveChoice("Take Out");
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
    }
}