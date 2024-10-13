package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.User;
import com.example.tp2_api_rest.ecommerceapi.responses.PaymentCompleteRequest;
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

        // Créer le PaymentIntent avec le service de paiement
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);

        // Extraire uniquement l'identifiant du PaymentIntent
        String paymentId = paymentIntent.getId();

        // Retourner l'identifiant dans la réponse
        return new ResponseEntity<>(paymentId, HttpStatus.OK);
    }


    @PostMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestBody PaymentCompleteRequest request)
            throws Exception {

        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Obtenir l'email de l'utilisateur
        String userEmail = currentUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) { // Vérification de null ou vide
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User email is missing");
        }

        // Confirmer le paiement en utilisant paymentIntentId et paymentMethodId depuis le DTO
        return paymentService.stripePayment(userEmail, request.getPaymentIntentId(), request.getPaymentMethodId());
    }


}