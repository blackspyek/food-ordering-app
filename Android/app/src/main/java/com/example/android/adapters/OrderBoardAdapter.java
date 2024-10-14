package com.example.android.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.adapters.viewholders.OrderBoardViewHolder;

import java.util.List;

public class OrderBoardAdapter extends RecyclerView.Adapter<OrderBoardViewHolder> {
    private List<String> orderNumberList;
    public OrderBoardAdapter(final List<String> orderNumberList) {
        this.orderNumberList = orderNumberList;
    }

    @NonNull
    @Override
    public OrderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_board_item, parent, false);
        return new OrderBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBoardViewHolder holder, int position) {
        String orderNumber = orderNumberList.get(position);
        holder.setOrderNumberTextView(orderNumber);
    }

    @Override
    public int getItemCount() {
        return orderNumberList != null ? orderNumberList.size() : 0;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<String> orderNumbers) {
        if (orderNumbers != null) {
            this.orderNumberList = orderNumbers;
            notifyDataSetChanged();
        }
    }
}
