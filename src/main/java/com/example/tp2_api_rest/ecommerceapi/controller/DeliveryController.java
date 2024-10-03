package com.example.tp2_api_rest.ecommerceapi.controller;

import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import com.example.tp2_api_rest.ecommerceapi.entity.Delivery;
import com.example.tp2_api_rest.ecommerceapi.entity.DeliveryStatus;
import com.example.tp2_api_rest.ecommerceapi.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // get a delivery entry
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable Integer orderId) {
        try {
            Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
            return ResponseEntity.ok(delivery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // update status
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<Delivery> updateDeliveryStatus(@PathVariable Long deliveryId, @RequestParam DeliveryStatus status) {
        try {
            Delivery updatedDelivery = deliveryService.updateDeliveryStatus(deliveryId, status);
            return ResponseEntity.ok(updatedDelivery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // update delivery address
    @PutMapping("/{deliveryId}/address")
    public ResponseEntity<Delivery> updateDeliveryAddress(@PathVariable Long deliveryId, @RequestBody Address address) {
        try {
            Delivery updatedDelivery = deliveryService.updateDeliveryAddress(deliveryId, address);
            return ResponseEntity.ok(updatedDelivery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
