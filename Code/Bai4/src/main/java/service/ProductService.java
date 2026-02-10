package com.example.Lab4.service;

import com.example.Lab4.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private List<Product> listProduct = new ArrayList<>();

    // 1. Lấy danh sách sản phẩm
    public List<Product> getAll() {
        return listProduct;
    }

    // 2. Lấy sản phẩm theo ID
    public Product get(int id) {
        return listProduct.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // 3. Thêm sản phẩm mới
    public void add(Product newProduct) {
        listProduct.add(newProduct);
    }

    // 4. Xử lý lưu ảnh
    public void updateImage(Product newProduct, MultipartFile imageProduct) {
        if (!imageProduct.isEmpty()) {
            try {
                // Lưu file vào thư mục target/classes/static/images
                Path dirImages = Paths.get("target/classes/static/images");
                if (!Files.exists(dirImages)) {
                    Files.createDirectories(dirImages);
                }

                String newFileName = UUID.randomUUID() + "_" + imageProduct.getOriginalFilename();
                Path pathFileUpload = dirImages.resolve(newFileName);

                Files.copy(imageProduct.getInputStream(), pathFileUpload, StandardCopyOption.REPLACE_EXISTING);

                newProduct.setImage(newFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 5. Cập nhật sản phẩm (Mới thêm)
    public void update(Product editProduct) {
        Product currentProduct = get(editProduct.getId());
        if (currentProduct != null) {
            currentProduct.setName(editProduct.getName());
            currentProduct.setPrice(editProduct.getPrice());
            currentProduct.setCategory(editProduct.getCategory());

            // Nếu người dùng chọn ảnh mới thì cập nhật, không thì giữ nguyên ảnh cũ
            if (editProduct.getImage() != null && !editProduct.getImage().isEmpty()) {
                currentProduct.setImage(editProduct.getImage());
            }
        }
    }

    // 6. Xóa sản phẩm (Mới thêm)
    public void delete(int id) {
        listProduct.removeIf(p -> p.getId() == id);
    }
}