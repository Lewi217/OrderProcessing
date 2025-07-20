package com.sanifu.order_processing.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderConfirmation {
    private String confirmationId;
    private Long orderId;
    private String customerName;
    private LocalDateTime confirmationDate;
    private List<OrderItem> items;
    private String deliveryAddress;
    private LocalDateTime estimatedDeliveryDate;
    private String additionalNotes;
    private String erpReferenceNumber;
}
