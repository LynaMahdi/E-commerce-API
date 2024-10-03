package com.example.tp2_api_rest.ecommerceapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long delivery_id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference // Prevent circular reference
    private Order order;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_address_id")
    private Address deliveryAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

}
