package com.example.tp2_api_rest.ecommerceapi.repository;


import com.example.tp2_api_rest.ecommerceapi.entity.Order;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {


    List<Order> findByUser(User user);

    @Override
    Optional<Order> findById(Integer integer);

    @Query("SELECT o FROM Order o WHERE o.order_id = ?1")
    Order findByOrderId(Integer orderId);

    void deleteAllByUser(User user);

}