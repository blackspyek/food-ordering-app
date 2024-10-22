package com.example.android.adapters.viewholders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.activities.BoardActivity;
import com.example.android.dto.CreateOrderResponse;
import com.example.android.dto.Data;
import com.example.android.models.OrderItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {

    private final TextView orderDetailsTextView;
    private final TextView orderBoardCodeTextView;
    private final Button buttonWaitingBoard;

    public OrderDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        orderDetailsTextView = itemView.findViewById(R.id.text_view_order_details);
        orderBoardCodeTextView = itemView.findViewById(R.id.text_view_board_code);
        buttonWaitingBoard = itemView.findViewById(R.id.buttonWaitingBoard);
        setupBoardButton();
    }

    private void setupBoardButton() {
        buttonWaitingBoard.setOnClickListener(v -> navigateToBoardActivity());
    }

    private void navigateToBoardActivity() {
        Context context = itemView.getContext();
        Intent intent = new Intent(context, BoardActivity.class);
        context.startActivity(intent);
    }

    public void bind(CreateOrderResponse orderDetails) {
        Data data = orderDetails.getData();
        orderBoardCodeTextView.setText(data.getBoardCode());
        orderDetailsTextView.setText(formatOrderDetails(data));
    }

    @SuppressLint("DefaultLocale")
    private String formatOrderDetails(Data data) {
        return String.format("%s\n\nTotal: $%.2f\n%s\n",
                getItemsString(data.getOrderItems()),
                data.getTotalPrice(),
                formatOrderDate(data.getOrderTime()));
    }

    private String formatOrderDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return formatDateOutput(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    private String formatDateOutput(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return outputFormat.format(date);
    }

    private String getItemsString(List<OrderItem> orderItems) {
        StringBuilder itemsStringBuilder = new StringBuilder();
        for (OrderItem item : orderItems) {
            appendOrderItem(itemsStringBuilder, item);
        }
        return itemsStringBuilder.toString().trim();
    }

    private void appendOrderItem(StringBuilder itemsStringBuilder, OrderItem item) {
        itemsStringBuilder.append(item.getQuantity())
                .append(" x ")
                .append(item.getItem().getName())
                .append("\n");
    }
}
