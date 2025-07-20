package com.sanifu.order_processing.exception;

public class ErpIntegrationException extends Exception {
    public ErpIntegrationException(String message) {
        super(message);
    }

    public ErpIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
