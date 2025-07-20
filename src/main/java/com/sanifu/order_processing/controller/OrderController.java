package com.sanifu.order_processing.controller;

import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderStatus;
import com.sanifu.order_processing.repository.OrderRepository;
import com.sanifu.order_processing.service.parsing.OrderParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderParserService orderParserService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (status != null) {
            return ResponseEntity.ok(orderRepository.findByStatus(status));
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(orderRepository.findByCreatedAtBetween(startDate, endDate));
        } else {
            return ResponseEntity.ok(orderRepository.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reprocess")
    public ResponseEntity<String> reprocessOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        order.setStatus(OrderStatus.RECEIVED);
        order.setErrorMessage(null);
        orderRepository.save(order);

        orderParserService.parseOrder(id);

        return ResponseEntity.ok("Order queued for reprocessing");
    }
}
