package com.sanifu.order_processing.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;
    private String sourceIdentifier; // WhatsApp number or email address

    @Enumerated(EnumType.STRING)
    private OrderSource source;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String rawContent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    private String customerName;
    private String customerContact;
    private String deliveryAddress;
    private LocalDateTime requestedDeliveryDate;

    private String notes;
    private String errorMessage;

    private String originalImageUrl;
    private String confirmationPdfUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
