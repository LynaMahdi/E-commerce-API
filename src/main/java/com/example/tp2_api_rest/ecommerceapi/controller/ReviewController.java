package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.Review;
import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.ProductRepository;
import com.example.tp2_api_rest.ecommerceapi.service.ProductService;
import com.example.tp2_api_rest.ecommerceapi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;

    // afficher les avis d'un produit donné
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsForProduct(@PathVariable Integer productId) throws NotFoundException {
        Product product= productService.getProductById(productId);
        List<Review> reviews = reviewService.getReviewsForProduct(product);
        return ResponseEntity.ok(reviews);
    }

    // permettre à un utilisateur de laisser un avis sur un produit acheté
    @PostMapping("/product/{productId}")
    public ResponseEntity<Review> addReviewForProduct(@PathVariable Integer productId, @RequestParam String comment, @RequestParam int rating) throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        Review review = reviewService.addReview(productId, currentUser, comment, rating);
        return ResponseEntity.ok(review);
    }

    //supprimer un avis inapproprié
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}