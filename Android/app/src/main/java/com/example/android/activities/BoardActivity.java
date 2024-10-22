package com.example.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.OrderBoardAdapter;
import com.example.android.api.OrderBoardCallback;
import com.example.android.api.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends BaseActivity implements OrderBoardCallback {

    private RecyclerView preparingRecyclerView;
    private RecyclerView readyRecyclerView;
    private OrderBoardAdapter preparingAdapter;
    private OrderBoardAdapter readyAdapter;
    private final List<String> preparingOrders = new ArrayList<>();
    private final List<String> readyOrders = new ArrayList<>();
    private WebSocketClient webSocketClient;
    private boolean isConnecting = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureActivityLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeButtons();
        initializeRecyclerViews();
        setupLogoClick();
        initializeAdapters();
        initializeWebSocketClient();
        connectWebSocket();
    }

    private void initializeWebSocketClient() {
        webSocketClient = new WebSocketClient();
        webSocketClient.setOrderBoardCallback(this);
    }

    private void connectWebSocket() {
        if (isConnecting) return;

        isConnecting = true;
        new Thread(this::attemptWebSocketConnection).start();
    }

    private void attemptWebSocketConnection() {
        try {
            webSocketClient.start();
            Thread.sleep(1000);
            onWebSocketConnected();
        } catch (Exception e) {
            onWebSocketConnectionFailed(e);
        }
    }

    private void onWebSocketConnected() {
        mainHandler.post(() -> isConnecting = false);
    }

    private void onWebSocketConnectionFailed(Exception e) {
        Log.e("WebSocket", "WebSocket connection failed", e);
        mainHandler.post(() -> isConnecting = false);
    }

    private void initializeRecyclerViews() {
        preparingRecyclerView = findViewById(R.id.preparingRecyclerView);
        readyRecyclerView = findViewById(R.id.readyRecyclerView);
    }

    private void initializeAdapters() {
        preparingAdapter = new OrderBoardAdapter(preparingOrders);
        readyAdapter = new OrderBoardAdapter(readyOrders);
        setupRecyclerView(preparingRecyclerView, preparingAdapter);
        setupRecyclerView(readyRecyclerView, readyAdapter);
    }

    private void setupRecyclerView(RecyclerView recyclerView, OrderBoardAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configureActivityLayout() {
        setContentView(R.layout.activity_board);
    }

    private void initializeButtons() {
        Button buttonBoard = findViewById(R.id.buttonBoard);
        Button buttonOrders = findViewById(R.id.buttonOrders);

        handleButtonsClick(buttonBoard, buttonOrders);

        triggerDefaultButtonClick(buttonBoard);
    }

    private void handleButtonsClick(Button buttonBoard, Button buttonOrder) {
        buttonBoard.setOnClickListener(v -> {
            updateButtonSelection(buttonBoard, buttonOrder);
        });

        buttonOrder.setOnClickListener(v -> {
            updateButtonSelection(buttonOrder, buttonBoard);
            closeWebSocketClient();
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            startActivity(intent);
        });
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeWebSocketClient();
    }

    private void closeWebSocketClient() {
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
    }

    @Override
    public void onOrderBoardUpdate(JSONObject orderBoard) {
        try {
            List<String> newPreparingOrders = parseOrderList(orderBoard, "liveOrderBoardCodes");
            List<String> newReadyOrders = parseOrderList(orderBoard, "liveOrderBoardReadyCodes");
            updateRecyclerViews(newPreparingOrders, newReadyOrders);
        } catch (JSONException e) {
            Log.e("WebSocket", "Error parsing order board data", e);
        }
    }

    private List<String> parseOrderList(JSONObject orderBoard, String key) throws JSONException {
        List<String> orders = new ArrayList<>();
        if (orderBoard.has(key)) {
            JSONArray orderArray = orderBoard.getJSONArray(key);
            for (int i = 0; i < orderArray.length(); i++) {
                orders.add(orderArray.getString(i));
            }
        }
        return orders;
    }

    private void updateRecyclerViews(List<String> newPreparingOrders, List<String> newReadyOrders) {
        mainHandler.post(() -> {
            updateOrderList(preparingOrders, newPreparingOrders, preparingAdapter);
            updateOrderList(readyOrders, newReadyOrders, readyAdapter);
        });
    }

    private void updateOrderList(List<String> currentOrders, List<String> newOrders, OrderBoardAdapter adapter) {
        currentOrders.clear();
        currentOrders.addAll(newOrders);
        adapter.updateData(currentOrders);
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