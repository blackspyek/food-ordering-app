package com.example.android.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.CheckoutAdapter;
import com.example.android.models.CartItem;
import java.util.ArrayList;
import java.util.Arrays;

public class CheckoutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity();
    }

    private void initializeActivity() {
        configureActivityLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        ArrayList<CartItem> cartItems = retrieveCartItems();
        initializeRecyclerView(cartItems);
    }

    private void configureActivityLayout() {
        setContentView(R.layout.activity_checkout);
    }

    private ArrayList<CartItem> retrieveCartItems() {
        CartItem[] cartItemArray = (CartItem[]) getIntent().getSerializableExtra("cartItems");
        return convertToArrayList(cartItemArray);
    }

    private ArrayList<CartItem> convertToArrayList(CartItem[] cartItemArray) {
        return (cartItemArray != null) ? new ArrayList<>(Arrays.asList(cartItemArray)) : new ArrayList<>();
    }


    private void initializeRecyclerView(ArrayList<CartItem> cartItems) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCheckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(this, cartItems);
        recyclerView.setAdapter(checkoutAdapter);
    }
}
