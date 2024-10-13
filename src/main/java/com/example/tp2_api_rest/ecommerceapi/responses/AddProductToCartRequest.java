package com.example.tp2_api_rest.ecommerceapi.responses;

public class AddProductToCartRequest {
    private Integer productId;
    private Integer quantity;

    // Getters et setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
