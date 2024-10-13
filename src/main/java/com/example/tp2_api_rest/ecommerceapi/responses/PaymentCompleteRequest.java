package com.example.tp2_api_rest.ecommerceapi.responses;

public class PaymentCompleteRequest {
    private String paymentIntentId;
    private String paymentMethodId;

    // Getters et setters
    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
