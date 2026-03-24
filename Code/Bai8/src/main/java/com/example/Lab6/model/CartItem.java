package com.example.Lab6.model;

import lombok.Data;

@Data
public class CartItem {
    private Long id;
    private String name;
    private String image;
    private Long price;
    private int quantity;

    public long getAmount() {
        return price == null ? 0 : price * quantity;
    }
}
