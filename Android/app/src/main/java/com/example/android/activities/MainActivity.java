package com.example.android.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.android.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fireBaseSubscribe();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        configureActionBar();

        // Check notification permission for API level >= 33
        askNotificationPermission();


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

    // Method to save user's dining choice
    private void saveChoice(String choice) {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("dining_option", choice);
        editor.apply();
    }

    // Method to navigate to the Menu screen
    private void navigateToMenu() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
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

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                fireBaseSubscribe();

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(this, "Please allow notification permission", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void fireBaseSubscribe() {
        // Subscribe to a topic
        FirebaseMessaging.getInstance().subscribeToTopic("News")
                .addOnCompleteListener(task -> {
                    String msg = getString(R.string.msg_subscribed);
                    if (!task.isSuccessful()) {
                        msg = getString(R.string.msg_subscribe_failed);
                    }
                    Log.d("MAIN", msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void configureActionBar() {
        super.configureActionBar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}
