package com.example.Lab4.controller;

import com.example.Lab4.model.Product;
import com.example.Lab4.service.CategoryService;
import com.example.Lab4.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    // 1. Hiển thị danh sách sản phẩm
    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("listproduct", productService.getAll());
        return "product/products";
    }

    // 2. Hiển thị form thêm mới
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAll());
        return "product/create";
    }

    // 3. Xử lý thêm mới
    @PostMapping("/create")
    public String create(@Valid Product newProduct,
                         BindingResult result,
                         @RequestParam("imageProduct") MultipartFile imageProduct,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", newProduct);
            model.addAttribute("categories", categoryService.getAll());
            return "product/create";
        }

        // Xử lý lưu ảnh
        productService.updateImage(newProduct, imageProduct);

        // Gán Category cho Product
        if(newProduct.getCategory() != null){
            newProduct.setCategory(categoryService.getCategoryById(newProduct.getCategory().getId()));
        }

        productService.add(newProduct);
        return "redirect:/products";
    }

    // 4. Hiển thị form chỉnh sửa (Mới thêm)
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        Product p = productService.get(id);
        if (p == null) {
            return "redirect:/products"; // Không tìm thấy thì quay về danh sách
        }
        model.addAttribute("product", p);
        model.addAttribute("categories", categoryService.getAll());
        return "product/edit";
    }

    // 5. Xử lý cập nhật (Mới thêm)
    @PostMapping("/edit")
    public String edit(@Valid Product editProduct,
                       BindingResult result,
                       @RequestParam("imageProduct") MultipartFile imageProduct,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            return "product/edit"; // Nếu lỗi validate thì ở lại trang sửa
        }

        // Xử lý ảnh (nếu người dùng có upload ảnh mới)
        if (imageProduct != null && !imageProduct.isEmpty()) {
            productService.updateImage(editProduct, imageProduct);
        }

        // Gán lại category
        if (editProduct.getCategory() != null) {
            editProduct.setCategory(categoryService.getCategoryById(editProduct.getCategory().getId()));
        }

        productService.update(editProduct);
        return "redirect:/products";
    }

    // 6. Xử lý xóa (Mới thêm)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        productService.delete(id);
        return "redirect:/products";
    }
}