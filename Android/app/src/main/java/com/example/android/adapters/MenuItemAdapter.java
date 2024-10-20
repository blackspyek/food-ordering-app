package com.example.android.adapters;
import com.example.android.adapters.viewholders.MenuItemViewHolder;
import com.example.android.adapters.viewholders.HeaderViewHolder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.R;
import com.example.android.models.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<MenuItem> menuItems;
    private String headerTitle;

    private final OnItemClickListener onItemClickListener;

    public MenuItemAdapter(List<MenuItem> menuItems, String headerTitle, OnItemClickListener listener) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
        this.headerTitle = headerTitle;
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
            return new MenuItemViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).textViewHeader.setText(headerTitle);
        } else {
            MenuItem menuItem = menuItems.get(position - 1);
            MenuItemViewHolder menuHolder = getMenuItemViewHolder((MenuItemViewHolder) holder);
            formatDishDetails(menuItem, menuHolder);
            loadImage(menuHolder, menuItem);
            handleMenuItemClick(menuHolder, menuItem);
        }
    }

    private void handleMenuItemClick(MenuItemViewHolder menuHolder, MenuItem menuItem) {
        menuHolder.bind(menuItem, onItemClickListener);
    }

    private static MenuItemViewHolder getMenuItemViewHolder(MenuItemViewHolder holder) {
        return holder;
    }

    @SuppressLint("DefaultLocale")
    private static void formatDishDetails(MenuItem menuItem, MenuItemViewHolder menuHolder) {
        String formattedName = menuItem.getName().replace(" ", "\n");
        menuHolder.textViewMenuItemName.setText(formattedName);
        menuHolder.textViewMenuItemPrice.setText(String.format("$%.2f", menuItem.getPrice()));
    }

    private static void loadImage(MenuItemViewHolder menuHolder, MenuItem menuItem) {
        Glide.with(menuHolder.itemView.getContext())
                .load(menuItem.getPhotoUrl())
                .placeholder(R.drawable.default_meal)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(menuHolder.imageViewMenuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size() + 1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMenuItems(List<MenuItem> newMenuItems, String headerTitle) {
        this.menuItems = newMenuItems;
        this.headerTitle = headerTitle;
        notifyDataSetChanged();
    }

}

