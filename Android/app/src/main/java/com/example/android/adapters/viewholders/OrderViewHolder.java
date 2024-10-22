package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView boardCodeTextView;
    public TextView orderIdTextView;
    public TextView totalPriceTextView;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        boardCodeTextView = itemView.findViewById(R.id.textViewBoardCode);
        orderIdTextView = itemView.findViewById(R.id.textViewOrderId);
        totalPriceTextView = itemView.findViewById(R.id.textViewTotalPrice);
    }
}
