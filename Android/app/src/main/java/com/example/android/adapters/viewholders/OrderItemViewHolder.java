package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.models.CartItem;

public class OrderItemViewHolder extends RecyclerView.ViewHolder {

    public TextView itemNameTextView;
    public TextView itemPriceTextView;

    public OrderItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemNameTextView = itemView.findViewById(R.id.itemTextView);
        itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
    }

    public void bind(CartItem item) {
        itemNameTextView.setText(item.getMenuItem().getName());
        itemPriceTextView.setText(String.format("$%.2f", item.getQuantity() * item.getMenuItem().getPrice()));
    }
}
