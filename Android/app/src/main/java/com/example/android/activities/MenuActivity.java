package com.example.android.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.MenuItemAdapter;
import com.example.android.api.ApiService;
import com.example.android.api.RetrofitClient;
import com.example.android.models.MenuItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends BaseActivity {
    private MenuItemAdapter menuItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureActivityLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeButtons();
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenuItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        createMenuItemAdapter();
        recyclerView.setAdapter(menuItemAdapter);
    }

    private void createMenuItemAdapter() {
        menuItemAdapter = new MenuItemAdapter(null, "", this::navigateToMenuItemDetails);
    }

    private void navigateToMenuItemDetails(MenuItem item) {
        Intent intent = new Intent(MenuActivity.this, MenuItemDetailActivity.class);
        intent.putExtra("MENU_ITEM", item);
        startActivity(intent);
    }

    private void configureActivityLayout() {
        setContentView(R.layout.activity_menu);
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

    private static void triggerDefaultButtonClick(Button buttonRamen) {
        buttonRamen.performClick();
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
            public void onResponse(@NonNull Call<List<MenuItem>> call, @NonNull Response<List<MenuItem>> response) {
                handleMenuItemsResponse(response, category);
            }

            @Override
            public void onFailure(@NonNull Call<List<MenuItem>> call, @NonNull Throwable t) {
                Log.e("MenuActivity", "Failed to fetch items: " + t.getMessage());
            }
        });
    }

    private void handleMenuItemsResponse(Response<List<MenuItem>> response, String category) {
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
}
