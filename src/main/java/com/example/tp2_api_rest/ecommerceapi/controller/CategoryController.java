package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.Category;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.RessourceExists;
import com.example.tp2_api_rest.ecommerceapi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) throws NotFoundException, com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException {
        Category category = categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("/addCategory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        // Vérifie si la catégorie existe déjà par son nom
        if (categoryService.categoryExistsByName(category.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Category already exists with name: " + category.getCategoryName());
        }

        Category savedCategory = categoryService.addCategory(category);

        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category categoryDetails) throws NotFoundException, com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException {
        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) throws NotFoundException, com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
