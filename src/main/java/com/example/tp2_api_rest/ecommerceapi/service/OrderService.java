package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.*;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.OrderRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.ProductRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Order createOrder(User user, List<OrderProduct> orderItems, String email) throws NotFoundException {
        Order order = new Order();
        order.setUser(user);
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("En traitement");

        double totalAmount = 0;

        for (OrderProduct item : orderItems) {
            Product product = productRepository.findById(item.getProduct().getProduct_id())
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID : " + item.getProduct().getProduct_id()));

            item.setOrder(order);
            item.setOrderedProductPrice(product.getPrice());
            totalAmount += item.getOrderedProductPrice() * item.getQuantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }


    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Integer orderId) throws NotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID : " + orderId));
    }

    public Order updateOrderStatus(Integer orderId, String status) throws NotFoundException {
        Order order = getOrderById(orderId);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Integer orderId) throws NotFoundException {
        Order order = getOrderById(orderId);
        if (!order.getOrderStatus().equals("Livrée")) {
            order.setOrderStatus("Annulée");
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Impossible d'annuler une commande déjà livrée");
        }
    }
}
