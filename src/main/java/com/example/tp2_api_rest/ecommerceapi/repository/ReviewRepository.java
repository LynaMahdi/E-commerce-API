package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.entity.Review;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Récupérer tous les avis pour un produit donné
    List<Review> findByProduct(Product product);

    // Récupérer tous les avis d'un utilisateur donné
    List<Review> findByUser(User user);

    // Supprimer un avis spécifique
    void deleteById(Long reviewId);
}
