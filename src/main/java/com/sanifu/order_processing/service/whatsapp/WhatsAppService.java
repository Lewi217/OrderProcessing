package com.sanifu.order_processing.service.whatsapp;

import com.sanifu.order_processing.config.WhatsAppConfig;
import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderSource;
import com.sanifu.order_processing.service.parsing.OrderParserService;
import com.sanifu.order_processing.model.OrderStatus;
import com.sanifu.order_processing.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {
    private final WhatsAppConfig whatsAppConfig;
    private final OrderRepository orderRepository;
    private final OrderParserService orderParserService;
    private final WhatsAppMessageParser messageParser;

    public boolean verifyWebhook(String mode, String token) {
        return "subscribe".equals(mode) && whatsAppConfig.getWebhookVerifyToken().equals(token);
    }

    public void processWhatsAppMessage(Map<String, Object> payload) {
        try {
            // Extract message data from payload
            String fromNumber = messageParser.extractSenderNumber(payload);
            String messageContent = messageParser.extractMessageText(payload);
            String messageId = messageParser.extractMessageId(payload);

            log.info("Received WhatsApp message from {}: {}", fromNumber, messageContent);

            // Create a new order with initial data
            Order order = new Order();
            order.setSource(OrderSource.WHATSAPP);
            order.setSourceIdentifier(fromNumber);
            order.setExternalId(messageId);
            order.setRawContent(messageContent);
            order.setStatus(OrderStatus.RECEIVED);

            // Save the initial order
            order = orderRepository.save(order);

            // Queue order for parsing
            orderParserService.parseOrder(order.getId());

        } catch (Exception e) {
            log.error("Error processing WhatsApp message", e);
        }
    }

    public void sendOrderConfirmation(String phoneNumber, String message) {
        // TODO: Implement sending confirmation back via WhatsApp API
        log.info("Sending order confirmation to {} via WhatsApp: {}", phoneNumber, message);
    }
}
