package com.example.Lab4.service;

// QUAN TRỌNG: Phải có dòng import này để lấy Category từ thư mục model sang
import com.example.Lab4.model.Category;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private List<Category> listCategory = new ArrayList<>();

    public CategoryService() {
        // Tạo dữ liệu giả
        listCategory.add(new Category(1, "Điện thoại"));
        listCategory.add(new Category(2, "Laptop"));
        listCategory.add(new Category(3, "Sách"));
    }

    public List<Category> getAll() {
        return listCategory;
    }

    public Category getCategoryById(int id){
        return listCategory.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}