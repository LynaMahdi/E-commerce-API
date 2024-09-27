package com.example.tp2_api_rest.ecommerceapi.responses;


import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import com.example.tp2_api_rest.ecommerceapi.entity.OrderProduct;

import java.util.List;

public class OrderRequest {

    private List<OrderProduct> orderItems;
    private String email;
    private Address address;

    // Getters and Setters

    public List<OrderProduct> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderProduct> orderItems) {
        this.orderItems = orderItems;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
