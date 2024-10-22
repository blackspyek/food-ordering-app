package com.example.android.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.dto.CreateOrderResponse;
import com.example.android.adapters.viewholders.OrderDetailsViewHolder;
import com.example.android.adapters.viewholders.HeaderViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ORDER_DETAIL = 1;

    private final Context context;
    private final List<CreateOrderResponse> orderDetails;
    private final long orderId;
    private final Gson gson;

    public OrderDetailsAdapter(Context context, long orderId) {
        this.context = context;
        this.orderId = orderId;
        this.gson = new Gson();
        this.orderDetails = fetchOrderDetails();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == VIEW_TYPE_HEADER ? R.layout.item_cart_header : R.layout.item_order;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return viewType == VIEW_TYPE_HEADER ? new HeaderViewHolder(view) : new OrderDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind("Order #" + orderId);
        } else if (holder instanceof OrderDetailsViewHolder) {
            CreateOrderResponse orderDetail = orderDetails.get(position - 1);
            ((OrderDetailsViewHolder) holder).bind(orderDetail);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetails.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ORDER_DETAIL;
    }


    /**
     * Retrieves previously saved order details from SharedPreferences.
     *
     * This method loads stored orders from the device's SharedPreferences and
     * deserializes them from JSON format using Gson. It then filters the orders
     * to find matching ones based on the order ID.
     *
     * @return A List of CreateOrderResponse objects containing matching order details
     *
     */
    private List<CreateOrderResponse> fetchOrderDetails() {
        SharedPreferences preferences = context.getSharedPreferences("user_orders", Context.MODE_PRIVATE);
        String existingOrdersJson = preferences.getString("orders", "[]");
        List<CreateOrderResponse> orders = gson.fromJson(existingOrdersJson, new TypeToken<List<CreateOrderResponse>>() {}.getType());

        return filterMatchingOrders(orders);
    }

    /**
     * Filters a list of orders to find those matching the current order ID.
     *
     * This method iterates through the provided list of orders and returns
     * only those that match the orderId field of the current context.
     *
     * @param orders The complete list of orders to filter
     * @return A List of CreateOrderResponse objects containing only the matching orders
     *
     */
    private List<CreateOrderResponse> filterMatchingOrders(List<CreateOrderResponse> orders) {
        List<CreateOrderResponse> matchingOrders = new ArrayList<>();
        for (CreateOrderResponse order : orders) {
            if (order.getData().getOrderId() == orderId) {
                matchingOrders.add(order);
            }
        }
        return matchingOrders;
    }
}
