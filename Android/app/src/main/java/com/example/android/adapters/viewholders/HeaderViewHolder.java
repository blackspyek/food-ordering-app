package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewHeader;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        bindViews(itemView);
    }

    private void bindViews(View itemView) {
        textViewHeader = itemView.findViewById(R.id.textViewMenuHeader);
    }
}
