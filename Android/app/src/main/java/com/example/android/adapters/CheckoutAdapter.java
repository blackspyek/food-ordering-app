package com.example.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.activities.OrderDetailsActivity;
import com.example.android.adapters.viewholders.CheckoutItemViewHolder;
import com.example.android.adapters.viewholders.HeaderViewHolder;
import com.example.android.adapters.viewholders.TotalWithInputsViewHolder;
import com.example.android.api.ApiService;
import com.example.android.api.RetrofitClient;
import com.example.android.dto.CreateOrderDto;
import com.example.android.dto.CreateOrderResponse;
import com.example.android.dto.OrderItemDto;
import com.example.android.managers.CartManager;
import com.example.android.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_TOTAL_WITH_INPUTS = 2;

    private final List<CartItem> cartItems;
    private final String headerTitle;
    private final ApiService apiService;

    public CheckoutAdapter(Context context, List<CartItem> cartItems) {
        this.cartItems = cartItems;
        this.headerTitle = context.getString(R.string.checkout_header);
        this.apiService = RetrofitClient.getInstance().create(ApiService.class);
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
        holder.buttonPayment.setOnClickListener(v -> processPayment(holder));
    }

    private void processPayment(TotalWithInputsViewHolder holder) {
        String name = holder.editTextName.getText().toString().trim();
        String email = holder.editTextEmail.getText().toString().trim();
        hideKeyboard(holder.itemView.getContext(), holder.itemView);

        if (isValidInput(name, email, holder)) {
            holder.textViewPleaseWait.setVisibility(View.VISIBLE);
            createOrder(holder, email);
        }
    }

    private void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isValidInput(String name, String email, TotalWithInputsViewHolder holder) {
        String validationError = validateInputs(name, email, holder.itemView.getContext());
        if (validationError != null) {
            displayError(holder.errorTextView, validationError);
            return false;
        }
        return true;
    }

    private void createOrder(TotalWithInputsViewHolder holder, String email) {
        CreateOrderDto createOrderDto = new CreateOrderDto(email, getOrderType(holder.itemView.getContext()), convertCartItemsToOrderItemDtos(cartItems));
        submitOrder(createOrderDto, holder);
    }

    private void submitOrder(CreateOrderDto createOrderDto, TotalWithInputsViewHolder holder) {
        apiService.createOrder(createOrderDto).enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                holder.textViewPleaseWait.setVisibility(View.GONE);
                handleOrderResponse(response, holder);
            }

            @Override
            public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                holder.textViewPleaseWait.setVisibility(View.GONE);
                displayError(holder.errorTextView, "Network error: " + t.getMessage());
            }
        });
    }

    private void handleOrderResponse(Response<CreateOrderResponse> response, TotalWithInputsViewHolder holder) {
        if (response.isSuccessful() && response.body() != null) {
            saveOrderToPreferences(holder.itemView.getContext(), response.body());
            clearCart();
            navigateToOrderDetails(holder.itemView.getContext(), response.body().getData().getOrderId());
        } else {
            displayError(holder.errorTextView, "Failed to create order: " + response.message());
        }
    }

    private void saveOrderToPreferences(Context context, CreateOrderResponse orderResponse) {
        SharedPreferences preferences = context.getSharedPreferences("user_orders", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        List<CreateOrderResponse> orders = getExistingOrders(preferences);

        orders.add(orderResponse);
        editor.putString("orders", new Gson().toJson(orders));
        editor.apply();
    }

    private List<CreateOrderResponse> getExistingOrders(SharedPreferences preferences) {
        String existingOrdersJson = preferences.getString("orders", "[]");
        return new Gson().fromJson(existingOrdersJson, new TypeToken<List<CreateOrderResponse>>() {}.getType());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearCart() {
        CartManager.getInstance().clearCart();
        notifyDataSetChanged();
    }

    private void displayError(TextView errorTextView, String message) {
        errorTextView.setText(message);
    }

    private List<OrderItemDto> convertCartItemsToOrderItemDtos(List<CartItem> cartItems) {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItemDtos.add(new OrderItemDto(cartItem.getMenuItem().getId(), cartItem.getQuantity()));
        }
        return orderItemDtos;
    }

    private double calculateTotal() {
        return cartItems.stream().mapToDouble(CheckoutAdapter::calculatePrice).sum();
    }

    private static double calculatePrice(CartItem item) {
        return item.getQuantity() * item.getMenuItem().getPrice();
    }

    private String validateInputs(String name, String email, Context context) {
        if (!isValidName(name)) {
            return context.getString(R.string.please_enter_a_valid_name);
        }
        if (!isValidEmail(email)) {
            return context.getString(R.string.please_enter_a_valid_email);
        }
        return null;
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().length() <= 20 && name.trim().matches("^[A-Za-z]+$");
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static String getOrderType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return preferences.getString("dining_option", "default");
    }

    private void navigateToOrderDetails(Context context, int orderId) {
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra("orderId", (long)orderId);
        context.startActivity(intent);
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
}
