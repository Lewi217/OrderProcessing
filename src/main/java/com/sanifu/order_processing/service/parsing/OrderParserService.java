package com.sanifu.order_processing.service.parsing;

import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderItem;
import com.sanifu.order_processing.model.OrderStatus;
import com.sanifu.order_processing.repository.OrderRepository;
import com.sanifu.order_processing.service.erp.ErpIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderParserService {
    private final OrderRepository orderRepository;
    private final RuleBasedParser ruleBasedParser;
    private final OrderValidator orderValidator;
    private final ErpIntegrationService erpIntegrationService;

    @Async
    public void parseOrder(Long orderId) {
        log.info("Starting to parse order: {}", orderId);

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            log.error("Order not found: {}", orderId);
            return;
        }

        Order order = orderOpt.get();
        try {
            order.setStatus(OrderStatus.PARSING);
            orderRepository.save(order);

            List<OrderItem> items = ruleBasedParser.parseOrderItems(order.getRawContent());

            items.forEach(item -> item.setOrder(order));
            order.setItems(items);
            order.setStatus(OrderStatus.PARSED);

            List<String> validationErrors = orderValidator.validateOrder(order);
            if (!validationErrors.isEmpty()) {
                order.setStatus(OrderStatus.VALIDATION_ERROR);
                order.setErrorMessage(String.join("; ", validationErrors));
                orderRepository.save(order);
                return;
            }

            order.setStatus(OrderStatus.VALIDATED);
            orderRepository.save(order);


            erpIntegrationService.pushOrderToErp(order.getId());

        } catch (Exception e) {
            log.error("Error parsing order: {}", orderId, e);
            order.setStatus(OrderStatus.VALIDATION_ERROR);
            order.setErrorMessage("Parsing error: " + e.getMessage());
            orderRepository.save(order);
        }
    }
}
