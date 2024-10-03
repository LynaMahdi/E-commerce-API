package com.example.tp2_api_rest.ecommerceapi.repository;


import com.example.tp2_api_rest.ecommerceapi.entity.Delivery;
import com.example.tp2_api_rest.ecommerceapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
        Delivery findByOrder(Order order);
}

