package com.example.Lab6.service;

import com.example.Lab6.model.CartItem;
import com.example.Lab6.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    public static final String CART_SESSION_KEY = "cart";
    private final HttpSession session;

    public CartService(HttpSession session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart() {
        Object data = session.getAttribute(CART_SESSION_KEY);
        if (data == null) {
            List<CartItem> cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
            return cart;
        }
        return (List<CartItem>) data;
    }

    public void addToCart(Product product) {
        List<CartItem> cart = getCart();
        for (CartItem item : cart) {
            if (item.getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        CartItem item = new CartItem();
        item.setId(product.getId());
        item.setName(product.getName());
        item.setImage(product.getImage());
        item.setPrice(product.getPrice());
        item.setQuantity(1);
        cart.add(item);
    }

    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }
        for (CartItem item : getCart()) {
            if (item.getId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeFromCart(Long productId) {
        getCart().removeIf(item -> item.getId().equals(productId));
    }

    public long getTotalMoney() {
        return getCart().stream().mapToLong(CartItem::getAmount).sum();
    }

    public int getTotalItems() {
        return getCart().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public void clearCart() {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
