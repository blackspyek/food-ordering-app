package com.example.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.OrderDetailsAdapter;

public class OrderDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity();
        setupLogoClick();
    }

    private void initializeActivity() {
        setContentView(R.layout.activity_order_details);
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        long orderId = extractOrderIdFromIntent();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        configureRecyclerView(recyclerView, orderId);
    }

    private long extractOrderIdFromIntent() {
        return getIntent().getLongExtra("orderId", -1);
    }

    private void configureRecyclerView(RecyclerView recyclerView, long orderId) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderDetailsAdapter(this, orderId));
    }

    @Override
    public boolean onSupportNavigateUp() {
        navigateToMenuActivity();
        return true;
    }

    private void navigateToMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
