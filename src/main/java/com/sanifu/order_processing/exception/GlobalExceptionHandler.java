package com.sanifu.order_processing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderParsingException.class)
    public ResponseEntity<Map<String, String>> handleOrderParsingException(OrderParsingException e) {
        log.error("Order parsing error", e);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Order parsing error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ErpIntegrationException.class)
    public ResponseEntity<Map<String, String>> handleErpIntegrationException(ErpIntegrationException e) {
        log.error("ERP integration error", e);
        Map<String, String> response = new HashMap<>();
        response.put("error", "ERP integration error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unexpected error", e);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
