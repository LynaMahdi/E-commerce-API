package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.*;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.responses.OrderRequest;
import com.example.tp2_api_rest.ecommerceapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;



    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam Integer cartId,@RequestParam Long paymentId, @RequestParam String paymentIntentId, @RequestBody Address address) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = ((User) authentication.getPrincipal()).getUser_id();


            Order createdOrder = orderService.createOrderAfterPaymentConfirmation(userId, cartId, paymentId,paymentIntentId,address);

            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer orderId, @RequestParam OrderStatus status) throws NotFoundException {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer orderId) throws NotFoundException {
        String cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) throws NotFoundException, com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }


}
