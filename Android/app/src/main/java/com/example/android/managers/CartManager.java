package com.example.android.managers;

import com.example.android.models.CartItem;
import com.example.android.models.MenuItem;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(MenuItem menuItem) {
        if (!incrementQuantityIfExists(menuItem)) {
            addNewCartItem(menuItem);
        }
    }

    private boolean incrementQuantityIfExists(MenuItem menuItem) {
        for (CartItem cartItem : cartItems) {
            if (isSameMenuItem(cartItem, menuItem)) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                return true;
            }
        }
        return false;
    }

    private boolean isSameMenuItem(CartItem cartItem, MenuItem menuItem) {
        return cartItem.getMenuItem().getName().equals(menuItem.getName());
    }

    private void addNewCartItem(MenuItem menuItem) {
        cartItems.add(new CartItem(menuItem, 1));
    }
}
