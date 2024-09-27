package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.Category;
import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.RessourceExists;
import com.example.tp2_api_rest.ecommerceapi.repository.CategoryRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Ajouter un produit
    public Product addProduct2(Integer categoryId, Product product) throws NotFoundException {
        // Fetch the category by ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));

        // Set the category for the product
        product.setCategory(category);

        // Save the product and return the saved entity
        return productRepository.save(product);
    }

    // Ajouter un produit
    public Product addProduct(Integer categoryId, Product product) throws NotFoundException {
        // Récupérer la catégorie par ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée avec l'ID : " + categoryId));

        // Vérifier si le produit existe déjà dans cette catégorie
        boolean productExists = category.getProducts().stream()
                .anyMatch(existingProduct ->
                        existingProduct.getName().equals(product.getName()) &&
                                existingProduct.getDescription().equals(product.getDescription())
                );

        if (productExists) {
            throw new IllegalStateException("Le produit existe déjà dans cette catégorie.");
        }

        // Si le produit n'existe pas, on l'ajoute
        product.setCategory(category);
        return productRepository.save(product);
    }

    public boolean checkProductExists(Integer categoryId, String productName, String productDescription) {
        return productRepository.findByNameAndDescriptionAndCategory(productName, productDescription, categoryRepository.findById(categoryId).orElse(null)) != null;
    }



    public Product getProductById(Integer id) throws NotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id " + id));
    }



    public Product updateProduct(Integer id, Product productDetails) throws NotFoundException {
        Product existingProduct = getProductById(id);

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStock(productDetails.getStock());
        existingProduct.setImage(productDetails.getImage());

        // mettre à jour la catégorie
        if (productDetails.getCategory() != null) {
            existingProduct.setCategory(productDetails.getCategory());
        }

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Integer id) throws NotFoundException {
        Product existingProduct = getProductById(id);
        productRepository.delete(existingProduct);
    }
}