package com.example.tp2_api_rest.ecommerceapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

  //  @Email
    @Column(nullable = false)
    private String email;
/*
    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<OrderItem> orderItems = new ArrayList<>();*/

    private LocalDate orderDate;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Double totalAmount;
    private String orderStatus;
}