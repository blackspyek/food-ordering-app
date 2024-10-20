package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

public class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
    public TextView itemTextView;
    public TextView itemPriceTextView;

    public CheckoutItemViewHolder(View itemView) {
        super(itemView);
        initializeElements(itemView);
    }

    private void initializeElements(View itemView) {
        itemTextView = itemView.findViewById(R.id.textViewItem);
        itemPriceTextView = itemView.findViewById(R.id.textViewItemPrice);
    }
}