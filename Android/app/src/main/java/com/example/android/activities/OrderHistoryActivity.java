package com.example.android.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.OrderHistoryAdapter;
import com.example.android.dto.CreateOrderResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends BaseActivity {

    private RecyclerView orderHistoryRecyclerView;
    private TextView emptyOrderHistoryTextView;
    private OrderHistoryAdapter orderHistoryAdapter;
    private final List<CreateOrderResponse> orderHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureActivityLayout();
        initializeRecyclerView();
        initializeEmptyOrderTextView();
        loadOrderHistoryFromPreferences();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeButtons();
        setupLogoClick();
    }

    private void configureActivityLayout() {
        setContentView(R.layout.activity_order_history);
    }

    private void initializeButtons() {
        Button buttonBoard = findViewById(R.id.buttonBoard);
        Button buttonOrders = findViewById(R.id.buttonOrders);

        handleButtonsClick(buttonBoard, buttonOrders);

        triggerDefaultButtonClick(buttonOrders);
    }

    private void handleButtonsClick(Button buttonBoard, Button buttonOrder) {
        buttonBoard.setOnClickListener(v -> {
            updateButtonSelection(buttonBoard, buttonOrder);
            Intent intent = new Intent(this, BoardActivity.class);
            startActivity(intent);
        });

        buttonOrder.setOnClickListener(v -> updateButtonSelection(buttonOrder, buttonBoard));
    }

    private static void triggerDefaultButtonClick(Button buttonBoard) {
        buttonBoard.performClick();
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

    private void initializeRecyclerView() {
        orderHistoryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        orderHistoryAdapter = new OrderHistoryAdapter(orderHistoryList);
        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryRecyclerView.setAdapter(orderHistoryAdapter);
    }

    private void initializeEmptyOrderTextView() {
        emptyOrderHistoryTextView = findViewById(R.id.emptyOrderHistoryTextView);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadOrderHistoryFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_orders", Context.MODE_PRIVATE);
        List<CreateOrderResponse> orders = getExistingOrders(preferences);

        orderHistoryList.clear();
        orderHistoryList.addAll(reverseOrderList(orders));

        if (orderHistoryList.isEmpty()) {
            showEmptyOrderMessage();
        } else {
            showOrderHistoryList();
        }
        orderHistoryAdapter.notifyDataSetChanged();
    }

    private void showEmptyOrderMessage() {
        orderHistoryRecyclerView.setVisibility(View.GONE);
        emptyOrderHistoryTextView.setVisibility(View.VISIBLE);
    }

    private void showOrderHistoryList() {
        orderHistoryRecyclerView.setVisibility(View.VISIBLE);
        emptyOrderHistoryTextView.setVisibility(View.GONE);
    }

    private List<CreateOrderResponse> reverseOrderList(List<CreateOrderResponse> orders) {
        List<CreateOrderResponse> reversedOrders = new ArrayList<>(orders);
        java.util.Collections.reverse(reversedOrders);
        return reversedOrders;
    }

    private List<CreateOrderResponse> getExistingOrders(SharedPreferences preferences) {
        String ordersJson = preferences.getString("orders", "[]");
        return parseOrdersFromJson(ordersJson);
    }


    private List<CreateOrderResponse> parseOrdersFromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<List<CreateOrderResponse>>() {
        }.getType());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return true;
    }

}
