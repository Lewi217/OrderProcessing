package com.sanifu.order_processing.service.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WhatsAppMessageParser {

    @SuppressWarnings("unchecked")
    public String extractSenderNumber(Map<String, Object> payload) {
        try {
            // Access entry as a List instead of casting to an array
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            Map<String, Object> entry = entries.get(0);

            // Access changes as a List
            List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
            Map<String, Object> change = changes.get(0);

            Map<String, Object> value = (Map<String, Object>) change.get("value");

            // Access messages as a List
            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            Map<String, Object> message = messages.get(0);

            // Get the sender's number directly
            return (String) message.get("from");
        } catch (Exception e) {
            log.error("Failed to extract sender number from WhatsApp payload", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String extractMessageText(Map<String, Object> payload) {
        try {
            // Access entry as a List
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            Map<String, Object> entry = entries.get(0);

            // Access changes as a List
            List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
            Map<String, Object> change = changes.get(0);

            Map<String, Object> value = (Map<String, Object>) change.get("value");

            // Access messages as a List
            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            Map<String, Object> message = messages.get(0);

            Map<String, Object> text = (Map<String, Object>) message.get("text");
            return (String) text.get("body");
        } catch (Exception e) {
            log.error("Failed to extract message text from WhatsApp payload", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String extractMessageId(Map<String, Object> payload) {
        try {
            // Access entry as a List
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            Map<String, Object> entry = entries.get(0);

            // Access changes as a List
            List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
            Map<String, Object> change = changes.get(0);

            Map<String, Object> value = (Map<String, Object>) change.get("value");

            // Access messages as a List
            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            Map<String, Object> message = messages.get(0);

            return (String) message.get("id");
        } catch (Exception e) {
            log.error("Failed to extract message ID from WhatsApp payload", e);
            return null;
        }
    }
}