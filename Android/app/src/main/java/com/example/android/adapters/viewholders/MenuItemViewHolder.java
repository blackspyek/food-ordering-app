package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.adapters.MenuItemAdapter;
import com.example.android.models.MenuItem;

public class MenuItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageViewMenuItem;
    public TextView textViewMenuItemName;
    public TextView textViewMenuItemPrice;

    public MenuItemViewHolder(View itemView) {
        super(itemView);
        bindViews(itemView);
    }

    private void bindViews(View itemView) {
        imageViewMenuItem = itemView.findViewById(R.id.imageViewMenuItem);
        textViewMenuItemName = itemView.findViewById(R.id.textViewMenuItemName);
        textViewMenuItemPrice = itemView.findViewById(R.id.textViewMenuItemPrice);
    }

    public void bind(final MenuItem menuItem, final MenuItemAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(menuItem));
    }
}