package com.example.android.adapters.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

public class TotalWithInputsViewHolder extends RecyclerView.ViewHolder {
    public TextView totalTextView;
    public EditText editTextName;
    public EditText editTextEmail;
    public Button buttonPayment;
    public TextView errorTextView;
    public TextView textViewPleaseWait;

    public TotalWithInputsViewHolder(View itemView) {
        super(itemView);
        initializeElements(itemView);
    }

    private void initializeElements(View itemView) {
        totalTextView = itemView.findViewById(R.id.textViewTotalPrice);
        editTextName = itemView.findViewById(R.id.editTextName);
        editTextEmail = itemView.findViewById(R.id.editTextEmail);
        buttonPayment = itemView.findViewById(R.id.buttonPayment);
        editTextName = itemView.findViewById(R.id.editTextName);
        editTextEmail = itemView.findViewById(R.id.editTextEmail);
        errorTextView = itemView.findViewById(R.id.errorTextView);
        textViewPleaseWait = itemView.findViewById(R.id.textViewPleaseWait);
    }
}