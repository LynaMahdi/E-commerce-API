package com.example.tp2_api_rest.ecommerceapi.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Integer cartId;


    @OneToOne
    @JoinColumn(name="user_id")
    @JsonBackReference("cart_user") // Prevent circular reference
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cart_cartItems")
    private List<CartProduct> cartItems = new ArrayList<>();

    private Double totalPrice;


}
