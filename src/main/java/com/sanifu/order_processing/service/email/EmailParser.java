package com.sanifu.order_processing.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailParser {

    public String extractName(String from, String subject, String body) {
        if (from == null || from.isEmpty()) {
            return "Unknown";
        }
        if (from.contains("<") && from.contains(">")) {
            return from.substring(0, from.indexOf("<")).trim();
        }
        return from;
    }

    public String extractOrderData(String subject, String body) {
        return body;
    }
}
