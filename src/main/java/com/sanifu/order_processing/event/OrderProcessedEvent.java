package com.sanifu.order_processing.event;

public class OrderProcessedEvent {
    private final Long orderId;

    public OrderProcessedEvent(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
