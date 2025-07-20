package com.sanifu.order_processing.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String productName;
    private String productCode;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;

    private String notes;
    private Boolean isAvailable;
}
