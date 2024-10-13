package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class OrderRequest {
    private Integer cartId;
    private Long paymentId;
    private String paymentIntentId;
    private Address address;

    // Getters et Setters
}
