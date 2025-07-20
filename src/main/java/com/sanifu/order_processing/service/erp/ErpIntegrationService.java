package com.sanifu.order_processing.service.erp;

import com.sanifu.order_processing.event.OrderProcessedEvent;
import com.sanifu.order_processing.exception.ErpIntegrationException;
import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderStatus;
import com.sanifu.order_processing.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErpIntegrationService {
    private final OrderRepository orderRepository;
    private final ErpAdapter erpAdapter;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    public void pushOrderToErp(Long orderId) {
        log.info("Pushing order to ERP: {}", orderId);

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            log.error("Order not found: {}", orderId);
            return;
        }

        Order order = orderOpt.get();
        try {
            String erpReferenceNumber = erpAdapter.pushOrder(order);

            order.setStatus(OrderStatus.PUSHED_TO_ERP);
            order.setNotes("ERP Reference: " + erpReferenceNumber);
            orderRepository.save(order);
            eventPublisher.publishEvent(new OrderProcessedEvent(order.getId()));
            log.info("Published OrderProcessedEvent for order: {}", order.getId());

        } catch (ErpIntegrationException e) {
            log.error("Error pushing order to ERP: {}", orderId, e);
            order.setStatus(OrderStatus.ERP_ERROR);
            order.setErrorMessage("ERP integration error: " + e.getMessage());
            orderRepository.save(order);
        }
    }
}

