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
        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.getAmount());
        params.put("currency", paymentInfoRequest.getCurrency());
        params.put("payment_method_types", List.of("card")); // Type de méthode de paiement

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Payment payment = new Payment();
        payment.setPaymentIntentId(paymentIntent.getId());
        payment.setUserEmail(paymentInfoRequest.getReceiptEmail());
        payment.setAmount(paymentInfoRequest.getAmount() / 100.0); // Convert from cents to dollars
        payment.setStatus("Waiting"); // Initial status

        paymentRepository.save(payment);

        return paymentIntent;
    }

    public ResponseEntity<String> stripePayment(String userEmail, String paymentIntentId, String paymentMethodId) throws Exception {
        // Log the email and payment intent ID being processed
        System.out.println("Processing payment for email: " + userEmail + " with PaymentIntent ID: " + paymentIntentId);

        // Find the payment record for the user
        Payment payment = paymentRepository.findByUserEmailAndPaymentIntentId(userEmail,paymentIntentId);
        if (payment == null) {
            throw new Exception("Payment information is missing");
        }

        try {
            // Confirm the payment intent with Stripe, using the payment method
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            Map<String, Object> confirmParams = new HashMap<>();
            confirmParams.put("payment_method", paymentMethodId); // Use the payment method ID

            paymentIntent = paymentIntent.confirm(confirmParams); // Confirm the payment with the method
            System.out.println("Payment Intent Status: " + paymentIntent.getStatus());

            // Update the payment record with the payment status
            payment.setAmount(paymentIntent.getAmount() / 100.0); // Convert amount to correct value (in dollars)
            payment.setStatus(paymentIntent.getStatus()); // Update status based on the payment intent
            payment.setPaymentMethod(paymentMethodId); // Set the payment method ID

            paymentRepository.save(payment); // Save updated payment info

            // Handle different payment statuses
            if ("succeeded".equals(paymentIntent.getStatus())) {
                return new ResponseEntity<>("Payment completed successfully.", HttpStatus.OK);
            } else if ("requires_action".equals(paymentIntent.getStatus())) {
                // If the payment requires additional actions (like 3D Secure), handle accordingly
                return new ResponseEntity<>("Payment requires additional authentication.", HttpStatus.PRECONDITION_REQUIRED);
            } else {
                return new ResponseEntity<>("Payment could not be completed: " + paymentIntent.getStatus(), HttpStatus.BAD_REQUEST);
            }
        } catch (StripeException e) {
            // Handle Stripe exceptions
            System.out.println("Stripe exception occurred: " + e.getMessage());
            throw new Exception("Payment could not be completed: " + e.getMessage());
        }
    }

}
