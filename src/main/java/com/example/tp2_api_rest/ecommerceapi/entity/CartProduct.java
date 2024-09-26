package com.example.tp2_api_rest.ecommerceapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CartProduct {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne
    @JoinTable(name="product_id")
    private Product product;


}
