package com.example.tp2_api_rest.ecommerceapi.responses;


import lombok.Data;

@Data
public class PaymentInfoRequest {
    private int amount;
    private String currency;
    private String receiptEmail;
}
