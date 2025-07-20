package com.sanifu.order_processing.service.erp;

import com.sanifu.order_processing.exception.ErpIntegrationException;
import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ErpAdapter {

    /**
     * Pushes an order to the ERP system
     * For MVP, this is just a simulation
     * In Phase 2, we'll implement actual ERP integration
     */
    public String pushOrder(Order order) throws ErpIntegrationException {
        try {
            log.info("Pushing order {} to ERP system", order.getId());

            Map<String, Object> erpPayload = new HashMap<>();
            erpPayload.put("orderId", order.getId());
            erpPayload.put("customerName", order.getCustomerName());
            erpPayload.put("customerContact", order.getCustomerContact());
            erpPayload.put("orderDate", order.getCreatedAt());
            erpPayload.put("deliveryAddress", order.getDeliveryAddress());

            List<Map<String, Object>> erpItems = order.getItems().stream()
                    .map(this::mapOrderItemToErpFormat)
                    .collect(Collectors.toList());
            erpPayload.put("items", erpItems);

            log.info("ERP Payload: {}", erpPayload);

            String erpReference = "ERP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            log.info("Order successfully pushed to ERP with reference: {}", erpReference);

            return erpReference;

        } catch (Exception e) {
            log.error("Failed to push order to ERP", e);
            throw new ErpIntegrationException("ERP integration failed: " + e.getMessage());
        }
    }

    private Map<String, Object> mapOrderItemToErpFormat(OrderItem item) {
        Map<String, Object> erpItem = new HashMap<>();
        erpItem.put("productName", item.getProductName());
        erpItem.put("productCode", item.getProductCode());
        erpItem.put("quantity", item.getQuantity());
        erpItem.put("unit", item.getUnit());
        erpItem.put("unitPrice", item.getUnitPrice());
        return erpItem;
    }
}
