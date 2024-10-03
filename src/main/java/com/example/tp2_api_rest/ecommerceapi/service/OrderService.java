package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.*;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private AddressRepository addressRepository;


    //create order
    public Order createOrderAfterPaymentConfirmation(Integer userId, Integer cartId, Long paymentId, String paymentIntentId, Address selectedAddress) throws NotFoundException, StripeException, Exception {
        Cart cart = cartRepository.findCartByUserAndCartId(userId, cartId);
        if (cart == null) {
            throw new NotFoundException("Cart not found");
        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new Exception("Payment not confirmed. Order cannot be created.");
        }

        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            throw new NotFoundException("Payment record not found");
        }


        // Vérifiez si l'adresse existe déjà dans la base de données
        Address existingAddress = addressRepository.findByStreetAndCityAndPostalCodeAndCountry(
                selectedAddress.getStreet(),
                selectedAddress.getCity(),
                selectedAddress.getPostalCode(),
                selectedAddress.getCountry()
        );

        // Si l'adresse existe, utilisez-la, sinon créez une nouvelle
        Address addressToUse;
        if (existingAddress != null) {
            System.out.println("coucou adresse nulles");
            addressToUse = existingAddress;
        } else {
            System.out.println("non je me suis trouvé hahaha");
            addressToUse = addressRepository.save(selectedAddress);
        }

        // Créer la commande avec l'adresse de livraison sélectionnée
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus(OrderStatus.PROCESSING);
        order.setPayment(payment);


        Order savedOrder = orderRepository.save(order);



        // Créer la livraison associée à la commande
        Delivery delivery = new Delivery();
        delivery.setOrder(savedOrder);
        delivery.setDeliveryAddress(selectedAddress);
        delivery.setDeliveryStatus(DeliveryStatus.PENDING); // Statut initial à PENDING
        deliveryRepository.save(delivery);


        savedOrder.setDelivery(delivery);

        List<CartProduct> cartItems = cart.getCartItems();
        List<CartProduct> itemsToDelete = new ArrayList<>(); // Liste des items à supprimer

        if (cartItems.isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }

        List<OrderProduct> orderItems = new ArrayList<>();
        for (CartProduct cartItem : cartItems) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(cartItem.getProduct());
            orderProduct.setQuantity(cartItem.getQuantity());
            orderProduct.setOrderedProductPrice(cartItem.getProductPrice());
            orderProduct.setOrder(savedOrder);
            orderItems.add(orderProduct);
            itemsToDelete.add(cartItem); // Ajoutez à la liste des items à supprimer
        }

        orderProductRepository.saveAll(orderItems);

        // Supprimez tous les produits du panier après avoir enregistré les commandes
        for (CartProduct cartItem : itemsToDelete) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            cartService.deleteProductFromCart(cartId, cartItem.getProduct().getProduct_id());
            product.setStock(product.getStock() - quantity);
        }

        return savedOrder;
    }



    //update order status
    public Order updateOrderStatus(Integer orderId, OrderStatus newStatus) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.setStatus(newStatus);

        // Vérifie si la commande a une livraison associée
        Delivery delivery = order.getDelivery();
        if (delivery != null) {
            if (newStatus.equals(OrderStatus.SHIPPED)) {
                delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
            } else if (newStatus.equals(OrderStatus.DELIVERED)) {
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
            }
            deliveryRepository.save(delivery);
        }

        return orderRepository.save(order);
    }

    //Cancel an order
    public Order cancelOrder(Integer orderId) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order that is already shipped or delivered");
        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }



}
