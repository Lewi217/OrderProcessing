package com.sanifu.order_processing.exception;

public class OrderParsingException extends RuntimeException {
    public OrderParsingException(String message) {
        super(message);
    }

    public OrderParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
