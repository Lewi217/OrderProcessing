package com.sanifu.order_processing.service.email;

import com.sanifu.order_processing.config.EmailConfig;
import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderSource;
import com.sanifu.order_processing.model.OrderStatus;
import com.sanifu.order_processing.repository.OrderRepository;
import com.sanifu.order_processing.service.parsing.OrderParserService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailConfig emailConfig;
    private final JavaMailSender mailSender;
    private final OrderRepository orderRepository;
    private final OrderParserService orderParserService;
    private final EmailParser emailParser;

    public void processIncomingEmail(String from, String subject, String body, MultipartFile[] attachments) {
        try {
            log.info("Processing email from: {}, subject: {}", from, subject);


            Order order = new Order();
            order.setSource(OrderSource.EMAIL);
            order.setSourceIdentifier(from);
            order.setExternalId(UUID.randomUUID().toString());
            order.setRawContent(body);
            order.setStatus(OrderStatus.RECEIVED);

            order.setCustomerName(emailParser.extractName(from, subject, body));
            order.setCustomerContact(from);

            order = orderRepository.save(order);

            if (attachments != null && attachments.length > 0) {
                // TODO: Save attachments and process images with OCR in Phase 2
                log.info("Email has {} attachments - will be processed in later phase", attachments.length);
            }

            orderParserService.parseOrder(order.getId());

        } catch (Exception e) {
            log.error("Error processing incoming email", e);
        }
    }

    public void sendOrderConfirmation(String email, String subject, byte[] pdfAttachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailConfig.getFromAddress());
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText("Thank you for your order. Please find your order confirmation attached.", false);

            // Attach PDF confirmation
            helper.addAttachment("order-confirmation.pdf", new ByteArrayResource(pdfAttachment));

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }
}
