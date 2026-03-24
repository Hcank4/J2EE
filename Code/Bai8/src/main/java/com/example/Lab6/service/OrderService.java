package com.example.Lab6.service;

import com.example.Lab6.model.CartItem;
import com.example.Lab6.model.OrderDetail;
import com.example.Lab6.model.OrderEntity;
import com.example.Lab6.model.Product;
import com.example.Lab6.repository.OrderRepository;
import com.example.Lab6.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderEntity createOrder(String customerName, String phone, String address, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalMoney(cartItems.stream().mapToLong(CartItem::getAmount).sum());

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm ID = " + item.getId()));

            if (product.getQuantity() == null) {
                product.setQuantity(0);
            }
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn");
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());

            order.getDetails().add(detail);
        }

        return orderRepository.save(order);
    }
}
