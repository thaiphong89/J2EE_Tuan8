package com.example.demo.service;

import com.example.demo.model.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SessionScope
public class CartService {
    private Map<Long, CartItem> cartItems = new HashMap<>();

    public void add(CartItem item) {
        CartItem existingItem = cartItems.get(item.getProductId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            cartItems.put(item.getProductId(), item);
        }
    }

    public void remove(Long productId) {
        cartItems.remove(productId);
    }

    public void update(Long productId, int quantity) {
        CartItem existingItem = cartItems.get(productId);
        if (existingItem != null) {
            if (quantity <= 0) {
                cartItems.remove(productId);
            } else {
                existingItem.setQuantity(quantity);
            }
        }
    }

    public void clear() {
        cartItems.clear();
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(cartItems.values());
    }

    public int getCount() {
        return cartItems.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getAmount() {
        return cartItems.values().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
}
