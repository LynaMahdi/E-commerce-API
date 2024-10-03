package com.example.tp2_api_rest.ecommerceapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer order_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @OneToMany(mappedBy = "order", cascade = { CascadeType.ALL})
    @JsonManagedReference
    private List<OrderProduct> orderItems = new ArrayList<>();

    private LocalDate orderDate;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @OneToOne
    @JoinColumn(name = "payment_id")
    @JsonManagedReference
    private Payment payment;

    private Double totalAmount;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonBackReference // Prevent circular reference
    private Delivery delivery;
}