package com.example.android.activities;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.example.android.R;
import com.example.android.adapters.CartAdapter;
import com.example.android.managers.CartManager;
import com.example.android.utils.SwipeToDeleteCallback;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BasketActivity extends BaseActivity implements CartAdapter.OnCartUpdateListener{
    private TextView textViewTotal;
    private CartAdapter cartAdapter;
    private TextView emptyBasketMessage;
    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureActivityLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeViews();
        initializeRecyclerView();
        updateTotalAmount();
        cartAdapter.updateEmptyMessageVisibility(emptyBasketMessage);
    }

    private void configureActivityLayout() {
        setContentView(R.layout.activity_basket);
    }

    private void initializeViews() {
        textViewTotal = findViewById(R.id.textViewTotal);
        emptyBasketMessage = findViewById(R.id.emptyBasketMessage);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        handleCheckOutButtonCLick();
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBasketItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        createCartAdapter(recyclerView);
        attachSwipeToDelete(recyclerView);
    }

    private void handleCheckOutButtonCLick() {
        buttonCheckout.setOnClickListener(v -> {
            Log.d("CheckOut button", "Checkout button clicked. Todo: navigate to checkout");
        });
    }

    private void createCartAdapter(RecyclerView recyclerView) {
        cartAdapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), this);
        recyclerView.setAdapter(cartAdapter);
    }

    private void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(cartAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressLint("DefaultLocale")
    private void updateTotalAmount() {
        textViewTotal.setText(String.format("Total: $%.2f", cartAdapter.calculateTotal()));
    }

    @Override
    public void onCartUpdated(double total) {
        updateTotalAmount();
        emptyBasketMessage.setVisibility(cartAdapter.getItemCount() == 1 ? View.VISIBLE : View.GONE);

    }


}