package com.sanifu.order_processing.model;

public enum OrderStatus {
    RECEIVED,
    PARSING,
    PARSED,
    VALIDATED,
    VALIDATION_ERROR,
    PUSHED_TO_ERP,
    ERP_ERROR,
    CONFIRMED,
    COMPLETED,
    REJECTED
}
