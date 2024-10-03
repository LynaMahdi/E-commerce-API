package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import com.example.tp2_api_rest.ecommerceapi.entity.Delivery;
import com.example.tp2_api_rest.ecommerceapi.entity.DeliveryStatus;
import com.example.tp2_api_rest.ecommerceapi.entity.Order;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.DeliveryRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Méthode pour suivre l'état de la livraison
    public Delivery getDeliveryByOrderId(Integer orderId) throws NotFoundException {
        Order order = orderRepository.findByOrderId(orderId);
        Delivery delivery = deliveryRepository.findByOrder(order);
        if (delivery == null) {
            throw new NotFoundException("Livraison non trouvée pour cette commande");
        }
        return delivery;
    }

    //update status
    public Delivery updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) throws Exception {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(deliveryId);
        if (!deliveryOptional.isPresent()) {
            throw new Exception("Livraison not  found");
        }
        Delivery delivery = deliveryOptional.get();
        delivery.setDeliveryStatus(newStatus);
        return deliveryRepository.save(delivery);
    }

    // update address
    public Delivery updateDeliveryAddress(Long deliveryId, Address newAddress) throws Exception {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(deliveryId);
        if (!deliveryOptional.isPresent()) {
            throw new Exception("Livraison non trouvée");
        }
        Delivery delivery = deliveryOptional.get();
        delivery.setDeliveryAddress(newAddress);
        return deliveryRepository.save(delivery);
    }
}
