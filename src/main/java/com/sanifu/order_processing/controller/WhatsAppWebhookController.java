package com.sanifu.order_processing.controller;

import com.sanifu.order_processing.service.whatsapp.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {
    private final WhatsAppService whatsAppService;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        log.info("Received WhatsApp webhook verification request: mode={}, token={}", mode, token);

        if (whatsAppService.verifyWebhook(mode, token)) {
            log.info("WhatsApp webhook verification successful");
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("WhatsApp webhook verification failed");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    @PostMapping
    public ResponseEntity<String> receiveMessage(@RequestBody Map<String, Object> payload) {
        log.info("Received WhatsApp webhook message");
        try {
            whatsAppService.processWhatsAppMessage(payload);
            return ResponseEntity.ok("Message received");
        } catch (Exception e) {
            log.error("Error processing WhatsApp webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing message");
        }
    }
}
