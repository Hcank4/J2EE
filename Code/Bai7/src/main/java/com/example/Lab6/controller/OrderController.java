package com.example.Lab6.controller;

import com.example.Lab6.model.OrderEntity;
import com.example.Lab6.service.CartService;
import com.example.Lab6.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final CartService cartService;
    private final OrderService orderService;

    public OrderController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkout(Model model, RedirectAttributes redirectAttributes) {
        if (cartService.getCart().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng đang trống");
            return "redirect:/cart";
        }
        model.addAttribute("cart", cartService.getCart());
        model.addAttribute("totalMoney", cartService.getTotalMoney());
        return "order/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            OrderEntity order = orderService.createOrder(customerName, phone, address, cartService.getCart());
            cartService.clearCart();
            model.addAttribute("order", order);
            return "order/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/checkout";
        }
    }
}
