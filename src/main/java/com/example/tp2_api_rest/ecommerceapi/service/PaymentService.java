package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.Payment;
import com.example.tp2_api_rest.ecommerceapi.repository.PaymentRepository;
import com.example.tp2_api_rest.ecommerceapi.responses.PaymentInfoRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {


    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.getAmount());
        params.put("currency", paymentInfoRequest.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }

    public ResponseEntity<String> stripePayment(String userEmail, String paymentIntentId) throws Exception {
        // Log the email and payment intent ID being processed
        System.out.println("Processing payment for email: " + userEmail + " with PaymentIntent ID: " + paymentIntentId);

        // Find the payment record for the user
        Payment payment = paymentRepository.findByUserEmail(userEmail);

        if (payment == null) {
            throw new Exception("Payment information is missing");
        }

        try {
            // Confirm the payment intent with Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            paymentIntent.confirm(); // Confirm the payment

            // Optionally, update the payment record with the payment status
            payment.setAmount(paymentIntent.getAmount() / 100.0); // Convert amount to correct value (in dollars)
            paymentRepository.save(payment); // Save updated payment info

            return new ResponseEntity<>("Payment completed successfully.", HttpStatus.OK);
        } catch (StripeException e) {
            // Handle Stripe exceptions
            System.out.println("Stripe exception occurred: " + e.getMessage());
            throw new Exception("Payment could not be completed: " + e.getMessage());
        }
    }




}
