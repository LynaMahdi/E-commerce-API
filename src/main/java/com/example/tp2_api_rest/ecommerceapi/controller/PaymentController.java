package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.User;
import com.example.tp2_api_rest.ecommerceapi.responses.PaymentInfoRequest;
import com.example.tp2_api_rest.ecommerceapi.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/secure")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfoRequest)
            throws StripeException {

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        String paymentStr = paymentIntent.toJson();

        return new ResponseEntity<>(paymentStr, HttpStatus.OK);
    }

    @PostMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestParam String paymentIntentId,
                                                        @RequestParam String paymentMethodId)
            throws Exception {
        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Get the email of the current user
        String userEmail = currentUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) { // Check for null or empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User email is missing");
        }

        // Confirm the payment using the paymentIntentId and paymentMethodId
        return paymentService.stripePayment(userEmail, paymentIntentId, paymentMethodId);
    }

}