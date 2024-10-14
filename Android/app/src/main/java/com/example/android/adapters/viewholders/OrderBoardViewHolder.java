package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

import lombok.Getter;
import lombok.Setter;


@Getter
public class OrderBoardViewHolder extends RecyclerView.ViewHolder {

    private final TextView orderNumberTextView;

    public OrderBoardViewHolder(View itemView) {
        super(itemView);
        orderNumberTextView = itemView.findViewById(R.id.order_number);
    }

    public void setOrderNumberTextView(String text) {
        orderNumberTextView.setText(text);
    }
}
