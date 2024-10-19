package com.example.android.adapters;

import com.example.android.adapters.viewholders.CartViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final List<CartItem> cartItems;
    private final String headerTitle;
    private final OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated(double total);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartUpdateListener listener) {
        this.cartItems = cartItems;
        this.headerTitle = context.getString(R.string.basket_header);
        this.cartUpdateListener = listener;
    }

    public double calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getMenuItem().getPrice() * item.getQuantity();
        }
        return total;
    }

    public void updateEmptyMessageVisibility(TextView emptyBasketMessage) {
        emptyBasketMessage.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = viewType == VIEW_TYPE_HEADER ? R.layout.item_cart_header : R.layout.item_cart;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return viewType == VIEW_TYPE_HEADER ? new HeaderViewHolder(view) : new CartViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(headerTitle);
        } else if (holder instanceof CartViewHolder) {
            ((CartViewHolder) holder).bind(cartItems.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public void removeItem(int position) {
        cartItems.remove(position - 1);
        notifyItemRemoved(position);
        notifyTotalUpdated();
    }

    public void notifyTotalUpdated() {
        if (cartUpdateListener != null) {
            cartUpdateListener.onCartUpdated(calculateTotal());
        }
    }

    public CartItem getItemAt(int position) {
        return cartItems.get(position - 1);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewCartHeader);
        }

        public void bind(String headerTitle) {
            textViewHeader.setText(headerTitle);
        }
    }

}
