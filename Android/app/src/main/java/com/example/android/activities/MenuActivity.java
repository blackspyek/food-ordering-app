package com.example.android.activities;
import com.example.android.models.MenuItem;
import com.example.android.adapters.MenuItemAdapter;
import com.example.android.R;
import com.example.android.api.ApiService;
import com.example.android.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MenuItemAdapter menuItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureActivityLayout();
        initializeButtons();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewMenuItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuItemAdapter = new MenuItemAdapter(null, "");
        recyclerView.setAdapter(menuItemAdapter);
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

    private void configureActivityLayout() {
        setContentView(R.layout.activity_menu);
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

    private void initializeButtons() {
        Button buttonRamen = findViewById(R.id.buttonRamen);
        Button buttonUdon = findViewById(R.id.buttonUdon);
        Button buttonRice = findViewById(R.id.buttonRice);

        handleButtonsClick(buttonRamen, buttonUdon, buttonRice);

        triggerDefaultButtonClick(buttonRamen);
    }

    private void handleButtonsClick(Button buttonRamen, Button buttonUdon, Button buttonRice) {
        buttonRamen.setOnClickListener(v -> {
            updateButtonSelection(buttonRamen, buttonUdon, buttonRice);
            fetchMenuItems("RAMEN");
        });

        buttonUdon.setOnClickListener(v -> {
            updateButtonSelection(buttonUdon, buttonRamen, buttonRice);
            fetchMenuItems("UDON_NOODLES");
        });

        buttonRice.setOnClickListener(v -> {
            updateButtonSelection(buttonRice, buttonRamen, buttonUdon);
            fetchMenuItems("RICE_NOODLES");
        });
    }

    private static boolean triggerDefaultButtonClick(Button buttonRamen) {
        return buttonRamen.performClick();
    }

    private void updateButtonSelection(Button selectedButton, Button... otherButtons) {
        setEnableButtonApperance(selectedButton);
        setDisableButtonsAppearance(otherButtons);
    }

    private static void setEnableButtonApperance(Button selectedButton) {
        setButtonAppearance(selectedButton, R.drawable.button_background_selected, android.graphics.Typeface.BOLD);
    }

    private static void setDisableButtonsAppearance(Button[] otherButtons) {
        for (Button button : otherButtons) {
            setButtonAppearance(button, R.drawable.button_background_default, android.graphics.Typeface.NORMAL);
        }
    }

    private static void setButtonAppearance(Button button, int backgroundResource, int typefaceStyle) {
        button.setBackgroundResource(backgroundResource);
        button.setTypeface(null, typefaceStyle);
    }


    private void fetchMenuItems(String category) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<MenuItem>> call = apiService.getMenuItemsByCategory(category);

        call.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                handleMenuItemsResponse(call, response, category);
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.e("MenuActivity", "Failed to fetch items: " + t.getMessage());
            }
        });
    }

    private void handleMenuItemsResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response, String category) {
        if (response.isSuccessful() && response.body() != null) {
            List<MenuItem> menuItems = response.body();
            List<MenuItem> availableMenuItems = filterAvailableItems(menuItems);
            String headerTitle = getHeaderTitleForCategory(category);
            menuItemAdapter.updateMenuItems(availableMenuItems, headerTitle);
        } else {
            Log.e("MenuActivity", "Response was not successful");
        }
    }

    private static @NonNull List<MenuItem> filterAvailableItems(List<MenuItem> menuItems) {
        List<MenuItem> availableMenuItems = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (item.isAvailable()) {
                availableMenuItems.add(item);
            }
        }
        return availableMenuItems;
    }

    private String getHeaderTitleForCategory(String category) {
        switch (category) {
            case "RAMEN":
                return "The Ramen Noodles";
            case "UDON_NOODLES":
                return "The Udon Noodles";
            case "RICE_NOODLES":
                return "The Rice Dishes";
            default:
                return "";
        }
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
}
