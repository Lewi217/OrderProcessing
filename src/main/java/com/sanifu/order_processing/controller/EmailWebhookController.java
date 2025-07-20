package com.sanifu.order_processing.controller;

import com.sanifu.order_processing.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/webhook/email")
@RequiredArgsConstructor
@Slf4j
public class EmailWebhookController {
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> receiveEmail(
            @RequestParam("from") String from,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments) {

        log.info("Received email webhook: from={}, subject={}", from, subject);

        try {
            emailService.processIncomingEmail(from, subject, body, attachments);
            return ResponseEntity.ok("Email received");
        } catch (Exception e) {
            log.error("Error processing email webhook", e);
            return ResponseEntity.internalServerError().body("Error processing email");
        }
    }
}
