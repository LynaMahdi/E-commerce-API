package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByPaymentIntentId(String paymentIntentId);
    Payment findByUserEmail(String userEmail);

    @Query("SELECT p FROM Payment p WHERE p.userEmail = ?1 AND p.paymentIntentId = ?2")
    Payment findByUserEmailAndPaymentIntentId(String userEmail, String paymentIntentId);
    Payment findByPaymentId(Long paymentId);


}
