package com.example.android.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class OrderBoardActivity extends BaseActivity implements OrderBoardCallback {

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
        initializeActivity();
        initializeWebSocketClient();
        connectWebSocket();
    }

    private void initializeActivity() {
        setContentView(R.layout.activity_order_board);
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeRecyclerViews();
        initializeAdapters();
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
            Thread.sleep(1000); // Wait for STOMP connection
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
        preparingRecyclerView = findViewById(R.id.inPrepareRecyclerView);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        hideBoardMenuItem(menu);
        return true;
    }

    private void hideBoardMenuItem(Menu menu) {
        MenuItem boardItem = menu.findItem(R.id.action_board);
        if (boardItem != null) {
            boardItem.setVisible(false);
        }
    }

}
