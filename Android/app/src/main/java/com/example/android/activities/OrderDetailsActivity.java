package com.example.android.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.OrderDetailsAdapter;

public class OrderDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivity();
        setupLogoClick();
    }

    private void setupActivity() {
        setContentView(R.layout.activity_order_details);
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        long orderId = getIntent().getLongExtra("orderId", -1);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderDetailsAdapter(this, orderId));
    }
}
