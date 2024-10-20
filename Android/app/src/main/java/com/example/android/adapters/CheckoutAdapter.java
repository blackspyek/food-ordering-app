package com.example.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.adapters.viewholders.CheckoutItemViewHolder;
import com.example.android.adapters.viewholders.TotalWithInputsViewHolder;
import com.example.android.R;
import com.example.android.models.CartItem;
import com.example.android.adapters.viewholders.HeaderViewHolder;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_TOTAL_WITH_INPUTS = 2;

    private final List<CartItem> cartItems;
    private final String headerTitle;

    public CheckoutAdapter(Context context, List<CartItem> cartItems) {
        this.cartItems = cartItems;
        this.headerTitle = context.getString(R.string.checkout_header);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        return createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPosition(position)) {
            bindHeader((HeaderViewHolder) holder);
        } else if (isTotalWithInputPosition(position)) {
            bindTotalWithInputs((TotalWithInputsViewHolder) holder);
        } else {
            bindItem((CheckoutItemViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return VIEW_TYPE_HEADER;
        } else if (isTotalWithInputPosition(position)) {
            return VIEW_TYPE_TOTAL_WITH_INPUTS;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    private int getLayoutId(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return R.layout.item_cart_header;
            case VIEW_TYPE_TOTAL_WITH_INPUTS:
                return R.layout.item_total_price;
            default:
                return R.layout.item_checkout;
        }
    }

    private RecyclerView.ViewHolder createViewHolder(View view, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(view);
            case VIEW_TYPE_TOTAL_WITH_INPUTS:
                return new TotalWithInputsViewHolder(view);
            default:
                return new CheckoutItemViewHolder(view);
        }
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    private boolean isTotalWithInputPosition(int position) {
        return position == cartItems.size() + 1;
    }

    private void bindHeader(HeaderViewHolder holder) {
        holder.bind(headerTitle);
    }

    @SuppressLint("DefaultLocale")
    private void bindTotalWithInputs(TotalWithInputsViewHolder holder) {
        holder.totalTextView.setText(String.format("Total: $%.2f", calculateTotal()));
        holder.buttonPayment.setOnClickListener(v -> handlePaymentClick());
    }

    private void handlePaymentClick() {
        Log.d("CheckoutAdapter", "Payment button clicked! Todo: navigate to payment");
    }

    @SuppressLint("DefaultLocale")
    private void bindItem(CheckoutItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position - 1);
        holder.itemTextView.setText(getCheckoutItemText(item));
        holder.itemPriceTextView.setText(String.format(" $%.2f", calculatePrice(item)));
    }

    private static String getCheckoutItemText(CartItem item) {
        return item.getQuantity() + "x " + item.getMenuItem().getName();
    }

    private static double calculatePrice(CartItem item) {
        return item.getQuantity() * item.getMenuItem().getPrice();
    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += calculatePrice(item);
        }
        return total;
    }
}
