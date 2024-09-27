package com.example.tp2_api_rest.ecommerceapi.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CartProduct {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cardProductId;

    @ManyToOne
    @JoinColumn(name="cart_id")
    @JsonBackReference
    private Cart cart;

    @ManyToOne
    @JoinTable(name="product_id")
    private Product product;

    private Integer quantity;

    private double productPrice;

}
