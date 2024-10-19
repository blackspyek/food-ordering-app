package com.example.android.adapters.viewholders;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.adapters.CartAdapter;
import com.example.android.models.CartItem;

public class CartViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageViewItem;
    private TextView textViewItemName;
    private EditText editTextItemQuantity;
    private TextView textViewItemPrice;
    private final CartAdapter cartAdapter;

    public CartViewHolder(View itemView, CartAdapter cartAdapter) {
        super(itemView);
        this.cartAdapter = cartAdapter;
        bindViews(itemView);
        setupQuantityChangeListener();
    }

    private void bindViews(View itemView) {
        imageViewItem = itemView.findViewById(R.id.imageViewItem);
        textViewItemName = itemView.findViewById(R.id.textViewItemName);
        editTextItemQuantity = itemView.findViewById(R.id.textViewItemQuantity);
        textViewItemPrice = itemView.findViewById(R.id.textViewItemPrice);
    }

    public void bind(CartItem cartItem) {
        textViewItemName.setText(formatItemName(cartItem));
        editTextItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        textViewItemPrice.setText(formatItemPrice(cartItem));
        loadImage(cartItem);
    }

    private String formatItemName(CartItem cartItem) {
        return cartItem.getMenuItem().getName().replace(" ", "\n");
    }

    @SuppressLint("DefaultLocale")
    private String formatItemPrice(CartItem cartItem) {
        double itemPrice = cartItem.getMenuItem().getPrice();
        int quantity = cartItem.getQuantity();
        double totalPrice = itemPrice * quantity;
        return String.format("$%.2f", totalPrice);
    }

    private void loadImage(CartItem cartItem) {
        Glide.with(itemView.getContext())
                .load(cartItem.getMenuItem().getPhotoUrl())
                .placeholder(R.drawable.default_meal)
                .into(imageViewItem);
    }

    private void setupQuantityChangeListener() {
        editTextItemQuantity.addTextChangedListener(new QuantityTextWatcher());
    }

    private class QuantityTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handleQuantityChange(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void handleQuantityChange(String quantityText) {
        if (!quantityText.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityText);
                if (quantity > 0) {
                    updateCartItemQuantity(quantity);
                    updateItemPrice(quantity);
                } else {
                    resetQuantityToDefault();
                }
            } catch (NumberFormatException e) {
                resetQuantityToDefault();
            }
        }
    }

    private void updateItemPrice(int quantity) {
        double itemPrice = cartAdapter.getItemAt(getBindingAdapterPosition()).getMenuItem().getPrice();
        double totalPrice = itemPrice * quantity;
        textViewItemPrice.setText(formatTotalPrice(totalPrice));
    }

    @SuppressLint("DefaultLocale")
    private String formatTotalPrice(double totalPrice) {
        return String.format("$%.2f", totalPrice);
    }

    private void updateCartItemQuantity(int quantity) {
        int position = getBindingAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            CartItem cartItem = cartAdapter.getItemAt(position);
            cartItem.setQuantity(quantity);
            cartAdapter.notifyTotalUpdated();
        }
    }

    private void resetQuantityToDefault() {
        showInvalidQuantityMessage();
        editTextItemQuantity.setText("1");
    }

    private void showInvalidQuantityMessage() {
        Toast.makeText(itemView.getContext(), R.string.quantityInputError, Toast.LENGTH_SHORT).show();
    }
}