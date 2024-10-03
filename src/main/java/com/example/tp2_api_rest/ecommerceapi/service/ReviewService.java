package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.entity.Review;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.ProductRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    // Récupérer les avis pour un produit donné
    public List<Review> getReviewsForProduct(Product product) {

        return reviewRepository.findByProduct(product);
    }

    // Ajouter un nouvel avis pour un produit
    public Review addReview(Integer productId, User user, String comment, int rating) throws NotFoundException {
        Product product= productService.getProductById(productId);
        if (product!=null) {

            // Create the new review
            Review review = new Review();
            review.setProduct(product);
            review.setUser(user);
            review.setComment(comment);
            review.setRating(rating);
            review.setReviewDate(LocalDate.now());

            // Get the existing list of reviews and add the new review
            List<Review> currentReviews = product.getReviews();
            if (currentReviews == null) {
                currentReviews = new ArrayList<>();
            }
            currentReviews.add(review);

            // Update the product's reviews list
            product.setReviews(currentReviews);

            // Save the review in the repository
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Product not found with id: " + productId);
        }
    }


    // Supprimer un avis spécifique
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
