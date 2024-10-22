package com.example.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.adapters.viewholders.OrderViewHolder;
import com.example.android.R;
import com.example.android.activities.OrderDetailsActivity;
import com.example.android.dto.CreateOrderResponse;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private final List<CreateOrderResponse> orderList;

    public OrderHistoryAdapter(List<CreateOrderResponse> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflateOrderItemView(parent);
        return new OrderViewHolder(view);
    }

    private View inflateOrderItemView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        CreateOrderResponse order = orderList.get(position);
        bindOrderData(holder, order);
        setItemClickListener(holder, order);
    }

    private void bindOrderData(OrderViewHolder holder, CreateOrderResponse order) {
        holder.boardCodeTextView.setText(order.getData().getBoardCode());
        holder.orderIdTextView.setText(formatOrderId(order.getData().getOrderId()));
        holder.totalPriceTextView.setText(formatTotalPrice(order.getData().getTotalPrice()));
    }

    private String formatOrderId(long orderId) {
        return "#" + orderId;
    }

    private String formatTotalPrice(double totalPrice) {
        return "$" + totalPrice;
    }

    private void setItemClickListener(OrderViewHolder holder, CreateOrderResponse order) {
        holder.itemView.setOnClickListener(v -> navigateToOrderDetails(holder.itemView.getContext(), order.getData().getOrderId()));
    }

    private void navigateToOrderDetails(Context context, long orderId) {
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra("orderId", orderId);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
