package com.example.tp2_api_rest.ecommerceapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private String comment;
    private int rating; //  de 1 à 5

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference("product-reviews")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-reviews")
    private User user;

    private LocalDate reviewDate;
}
