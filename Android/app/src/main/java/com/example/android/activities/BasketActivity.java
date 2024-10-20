package com.example.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.example.android.R;
import com.example.android.adapters.CartAdapter;
import com.example.android.managers.CartManager;
import com.example.android.models.CartItem;
import com.example.android.utils.SwipeToDeleteCallback;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class BasketActivity extends BaseActivity implements CartAdapter.OnCartUpdateListener {

    private TextView textViewTotal;
    private CartAdapter cartAdapter;
    private TextView emptyBasketMessage;
    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivity();
    }

    private void setupActivity() {
        configureActivityLayout();
        setupToolbar();
        configureActionBar();
        setupBackButtonHandler();
        initializeViews();
        setupRecyclerView();
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
        setupCheckoutButton();
    }

    private void setupCheckoutButton() {
        buttonCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void handleCheckout() {
        if (isCartEmpty()) {
            showEmptyCartToast();
        } else {
            navigateToCheckout();
        }
    }

    private boolean isCartEmpty() {
        return cartAdapter.getItemCount() == 1;
    }

    private void showEmptyCartToast() {
        Toast.makeText(this, R.string.no_items_in_cart, Toast.LENGTH_SHORT).show();
    }

    private void navigateToCheckout() {
        Intent checkoutIntent = createCheckoutIntent(getCartItemsArray());
        startActivity(checkoutIntent);
    }

    private CartItem[] getCartItemsArray() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        return cartItems.toArray(new CartItem[0]);
    }

    private Intent createCheckoutIntent(CartItem[] cartItems) {
        Intent intent = new Intent(BasketActivity.this, CheckoutActivity.class);
        intent.putExtra("cartItems", cartItems);
        return intent;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBasketItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupCartAdapter(recyclerView);
        enableSwipeToDelete(recyclerView);
    }

    private void setupCartAdapter(RecyclerView recyclerView) {
        cartAdapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), this);
        recyclerView.setAdapter(cartAdapter);
    }

    private void enableSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(cartAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressLint("DefaultLocale")
    private void updateTotalAmount() {
        String totalText = String.format("Total: $%.2f", cartAdapter.calculateTotal());
        textViewTotal.setText(totalText);
    }

    @Override
    public void onCartUpdated(double total) {
        updateTotalAmount();
        updateEmptyBasketMessageVisibility();
    }

    private void updateEmptyBasketMessageVisibility() {
        int visibility = cartAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE;
        emptyBasketMessage.setVisibility(visibility);
    }
}
