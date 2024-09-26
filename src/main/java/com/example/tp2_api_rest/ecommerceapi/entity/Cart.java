package com.example.tp2_api_rest.ecommerceapi.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="cart")
public class Cart {

    @Id
    @GeneratedValue
    Integer id;


    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private List<CartProduct> cartItems = new ArrayList<>();

    private Double totalPrice;


}
