package com.sanifu.order_processing.service.parsing;

import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderValidator {

    public List<String> validateOrder(Order order) {
        List<String> errors = new ArrayList<>();

        if (order.getItems() == null || order.getItems().isEmpty()) {
            errors.add("Order contains no items");
        }

        if (order.getItems() != null) {
            for (int i = 0; i < order.getItems().size(); i++) {
                OrderItem item = order.getItems().get(i);

                if (item.getProductName() == null || item.getProductName().trim().isEmpty()) {
                    errors.add("Item #" + (i+1) + " has no product name");
                }


                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    errors.add("Item #" + (i+1) + " (" + item.getProductName() + ") has invalid quantity");
                }
            }
        }

        return errors;
    }
}