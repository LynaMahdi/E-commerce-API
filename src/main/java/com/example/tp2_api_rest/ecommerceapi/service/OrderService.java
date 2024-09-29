package com.example.tp2_api_rest.ecommerceapi.service;

import com.example.tp2_api_rest.ecommerceapi.entity.*;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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

    public Order createOrderAfterPaymentConfirmation(Integer userId, Integer cartId, Long paymentId, String paymentIntentId) throws NotFoundException, StripeException, Exception {
        Cart cart = cartRepository.findCartByUserAndCartId(userId, cartId);
        if (cart == null) {
            throw new NotFoundException("Cart not found");
        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new Exception("Payment not confirmed. Order cannot be created.");
        }

        // Step 3: Retrieve the existing payment record from the database using both paymentId and paymentIntentId
        Payment payment = paymentRepository.findByPaymentId(paymentId); // This should match your method
        System.out.println("Payment Intent ID: " + paymentIntentId);

        if (payment == null) {
            throw new NotFoundException("Payment record not found");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setPayment(payment);

        // Save the order in the repository
        Order savedOrder = orderRepository.save(order);

        List<CartProduct> cartItems = cart.getCartItems();
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
        }

        orderProductRepository.saveAll(orderItems);

        for (CartProduct cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // Remove product from cart and update stock
            cartService.deleteProductFromCart(cartId, product.getProduct_id());
            product.setStock(product.getStock() - quantity);
        }

        return order;
    }


}
