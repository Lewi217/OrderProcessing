package com.sanifu.order_processing.service.parsing;

import com.sanifu.order_processing.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RuleBasedParser {

    // Simple regex patterns for MVP
    private final Pattern itemPattern = Pattern.compile("(\\d+)\\s*(?:x|pcs|pieces)?\\s*([\\w\\s]+)(?:\\s*\\$?(\\d+(?:\\.\\d+)?))?");

    public List<OrderItem> parseOrderItems(String rawContent) {
        List<OrderItem> items = new ArrayList<>();

        // Split content by lines
        String[] lines = rawContent.split("\\r?\\n");

        for (String line : lines) {
            Matcher matcher = itemPattern.matcher(line.trim());

            if (matcher.find()) {
                OrderItem item = new OrderItem();

                // Extract quantity
                String quantityStr = matcher.group(1);
                item.setQuantity(Integer.parseInt(quantityStr));

                // Extract product name
                String productName = matcher.group(2).trim();
                item.setProductName(productName);

                // Extract price if available
                if (matcher.groupCount() >= 3 && matcher.group(3) != null) {
                    String priceStr = matcher.group(3);
                    item.setUnitPrice(new BigDecimal(priceStr));
                }

                // Default values
                item.setUnit("pcs");
                item.setIsAvailable(true);

                items.add(item);
            }
        }

        log.info("Parsed {} items from order content", items.size());
        return items;
    }
}
