package com.example.efood_ui.client;

// Simple model to hold cart item info
public class CartItem {
    public String name;
    public double price;
    public int quantity;
    public int available;

    public CartItem(String name, double price, int available) {
        this.name = name;
        this.price = price;
        this.available = available;
        this.quantity = 0;
    }
}
