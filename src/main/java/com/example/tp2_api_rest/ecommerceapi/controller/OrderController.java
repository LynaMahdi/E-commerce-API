package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.*;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.responses.OrderRequest;
import com.example.tp2_api_rest.ecommerceapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = ((User) authentication.getPrincipal()).getUser_id();

            // Utilisez les informations de l'objet orderRequest pour créer la commande
            Order createdOrder = orderService.createOrderAfterPaymentConfirmation(
                    orderRequest.getPaymentId(),
                    orderRequest.getPaymentIntentId(),
                    orderRequest.getAddress()
            );

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) throws NotFoundException, com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }


    @GetMapping("/myOrders")
    public List<Order> allMyOrders() {
        List<Order> orders = orderService.getMyOrders();

        return (orders);
    }

}
