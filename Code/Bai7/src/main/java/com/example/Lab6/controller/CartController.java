package com.example.Lab6.controller;

import com.example.Lab6.model.Product;
import com.example.Lab6.service.CartService;
import com.example.Lab6.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final ProductService productService;
    private final CartService cartService;

    public CartController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cart", cartService.getCart());
        model.addAttribute("totalMoney", cartService.getTotalMoney());
        model.addAttribute("cartCount", cartService.getTotalItems());
        return "cart/list";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(id);
        if (product != null) {
            cartService.addToCart(product);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
        }
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long id, @RequestParam int quantity) {
        cartService.updateQuantity(id, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}
