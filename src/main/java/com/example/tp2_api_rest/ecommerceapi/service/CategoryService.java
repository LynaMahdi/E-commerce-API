package com.example.tp2_api_rest.ecommerceapi.service;


import com.example.tp2_api_rest.ecommerceapi.entity.Category;
import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) throws NotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));
    }


    public boolean categoryExistsByName(String categoryName) {
        return categoryRepository.findAll().stream()
                .anyMatch(category -> category.getCategoryName().equalsIgnoreCase(categoryName));

    }
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }


    public Category updateCategory(Integer id, Category categoryDetails) throws NotFoundException {
        Category existingCategory = getCategoryById(id);
        existingCategory.setCategoryName(categoryDetails.getCategoryName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Integer id) throws NotFoundException {
        Category cat=categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));

        List<Product> products=cat.getProducts();

        products.forEach(product ->
        {
            try {
                productService.deleteProductById(product.getProduct_id());
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        });


        categoryRepository.delete(cat);
    }

}
