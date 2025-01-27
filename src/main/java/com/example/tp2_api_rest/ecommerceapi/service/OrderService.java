package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.*;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Order createOrderAfterPaymentConfirmation(Long paymentId, String paymentIntentId, Address selectedAddress) throws NotFoundException, StripeException, Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userId=currentUser.getUser_id();
        Cart cart=cartRepository.findByUser(userId);
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
            addressToUse = existingAddress;
        } else {
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
        delivery.setDeliveryAddress(addressToUse);
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
        System.out.println("les trucs a supprimer :"+ itemsToDelete);
        // Supprimez tous les produits du panier après avoir enregistré les commandes
        for (CartProduct cartItem : itemsToDelete) {
            Product product = cartItem.getProduct();

            int quantity = cartItem.getQuantity();

            // Mise à jour du stock
            product.setStock(product.getStock() - quantity);

            // Suppression de l'élément du panier
            cartService.deleteProductFromCart(cartItem.getProduct().getProduct_id(),quantity);
        }

        return savedOrder;
    }



    //update order status
    public Order updateOrderStatus(Integer orderId, OrderStatus newStatus) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Mettre à jour le statut de la commande
        order.setStatus(newStatus);

        // Vérifier si une livraison est associée à la commande
        Delivery delivery = order.getDelivery();
        if (delivery != null) {
            switch (newStatus) {
                case PROCESSING:
                    delivery.setDeliveryStatus(DeliveryStatus.PENDING);
                    break;
                case SHIPPED:
                    delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
                    break;
                case DELIVERED:
                    delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown order status: " + newStatus);
            }
            deliveryRepository.save(delivery);
        }

        return orderRepository.save(order);
    }


    //Cancel an order
    public String cancelOrder(Integer orderId) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Vérifier si la commande est éligible
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            return "Cannot cancel an order that has already been shipped or delivered";
        }

        // Mettre à jour le statut de la commande
        order.setStatus(OrderStatus.CANCELLED);

        // Mettre à jour le statut de livraison si une livraison est associée
        Delivery delivery = order.getDelivery();
        if (delivery != null) {
            delivery.setDeliveryStatus(DeliveryStatus.CANCELED);
            deliveryRepository.save(delivery);
        }

        orderRepository.save(order);
        return "Order has been successfully cancelled";

    }


    //Get all the orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    //Get a specific Order
    public Order getOrderById(Integer id) throws NotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id));
    }



    public List<Order> getMyOrders(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return  orderRepository.findByUser(currentUser);



    }



}
