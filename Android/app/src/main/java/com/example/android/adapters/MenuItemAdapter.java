package com.example.android.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

    private OnItemClickListener onItemClickListener;

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        MenuItemViewHolder menuHolder = holder;
        return menuHolder;
    }

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

    public void updateMenuItems(List<MenuItem> newMenuItems, String headerTitle) {
        this.menuItems = newMenuItems;
        this.headerTitle = headerTitle;
        notifyDataSetChanged();
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewHeader;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewMenuHeader);
        }
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewMenuItem;
        public TextView textViewMenuItemName;
        public TextView textViewMenuItemPrice;

        public MenuItemViewHolder(View itemView) {
            super(itemView);
            imageViewMenuItem = itemView.findViewById(R.id.imageViewMenuItem);
            textViewMenuItemName = itemView.findViewById(R.id.textViewMenuItemName);
            textViewMenuItemPrice = itemView.findViewById(R.id.textViewMenuItemPrice);
        }

        public void bind(final MenuItem menuItem, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(menuItem));
        }
    }
}

