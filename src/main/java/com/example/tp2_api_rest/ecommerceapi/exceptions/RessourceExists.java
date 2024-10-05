package com.example.tp2_api_rest.ecommerceapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RessourceExists extends RuntimeException {
    public RessourceExists(String message) {
        super(message);
    }


}
