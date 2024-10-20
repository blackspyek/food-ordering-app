package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewHeader;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewHeader = itemView.findViewById(R.id.textViewHeader);
    }

    public void bind(String headerTitle) {
        textViewHeader.setText(headerTitle);
    }
}
