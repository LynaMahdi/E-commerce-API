package com.example.tp2_api_rest.ecommerceapi.exceptions;

public class RessourceExists extends RuntimeException {
    public RessourceExists(String message) {
        super(message);
    }
}
