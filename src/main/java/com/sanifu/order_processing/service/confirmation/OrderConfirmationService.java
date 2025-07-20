package com.sanifu.order_processing.service.confirmation;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.sanifu.order_processing.event.OrderProcessedEvent;
import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderConfirmation;
import com.sanifu.order_processing.model.OrderSource;
import com.sanifu.order_processing.model.OrderStatus;
import com.sanifu.order_processing.repository.OrderRepository;
import com.sanifu.order_processing.service.email.EmailService;
import com.sanifu.order_processing.service.whatsapp.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationService {
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    @EventListener
    public void handleOrderProcessed(OrderProcessedEvent event) {
        log.info("Received OrderProcessedEvent for order: {}", event.getOrderId());
        generateAndSendConfirmation(event.getOrderId());
    }

    @Async
    public void generateAndSendConfirmation(Long orderId) {
        log.info("Generating confirmation for order: {}", orderId);

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            log.error("Order not found: {}", orderId);
            return;
        }

        Order order = orderOpt.get();
        try {
            OrderConfirmation confirmation = createConfirmation(order);
            byte[] pdfBytes = generateConfirmationPdf(confirmation);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setConfirmationPdfUrl("/confirmations/" + confirmation.getConfirmationId() + ".pdf");
            orderRepository.save(order);

            // Send confirmation based on source
            if (order.getSource() == OrderSource.EMAIL) {
                emailService.sendOrderConfirmation(
                        order.getSourceIdentifier(),
                        "Your Order #" + order.getId() + " Confirmation",
                        pdfBytes
                );
            } else if (order.getSource() == OrderSource.WHATSAPP) {
                String message = createWhatsAppConfirmationMessage(confirmation);
                whatsAppService.sendOrderConfirmation(order.getSourceIdentifier(), message);
            }

            log.info("Order confirmation sent successfully");

        } catch (Exception e) {
            log.error("Error generating confirmation for order: {}", orderId, e);
            order.setErrorMessage("Confirmation error: " + e.getMessage());
            orderRepository.save(order);
        }
    }

    private OrderConfirmation createConfirmation(Order order) {
        OrderConfirmation confirmation = new OrderConfirmation();
        confirmation.setConfirmationId("CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        confirmation.setOrderId(order.getId());
        confirmation.setCustomerName(order.getCustomerName());
        confirmation.setConfirmationDate(LocalDateTime.now());
        confirmation.setItems(order.getItems());
        confirmation.setDeliveryAddress(order.getDeliveryAddress());
        confirmation.setEstimatedDeliveryDate(order.getRequestedDeliveryDate() != null ?
                order.getRequestedDeliveryDate() : LocalDateTime.now().plusDays(3));
        confirmation.setAdditionalNotes(order.getNotes());
        confirmation.setErpReferenceNumber(order.getNotes() != null && order.getNotes().startsWith("ERP Reference: ") ?
                order.getNotes().substring("ERP Reference: ".length()) : "");

        return confirmation;
    }

    private byte[] generateConfirmationPdf(OrderConfirmation confirmation) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        document.add(new Paragraph("Order Confirmation", titleFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Confirmation #: " + confirmation.getConfirmationId(), normalFont));
        document.add(new Paragraph("Order #: " + confirmation.getOrderId(), normalFont));
        document.add(new Paragraph("Date: " +
                confirmation.getConfirmationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
        document.add(new Paragraph("Customer: " + confirmation.getCustomerName(), normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Order Items:", headerFont));
        document.add(new Paragraph("-------------------------------------------", normalFont));

        for (int i = 0; i < confirmation.getItems().size(); i++) {
            var item = confirmation.getItems().get(i);
            document.add(new Paragraph((i+1) + ". " + item.getProductName() + " - " +
                    item.getQuantity() + " " + (item.getUnit() != null ? item.getUnit() : "pcs"), normalFont));
        }

        document.add(new Paragraph("-------------------------------------------", normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Delivery Information:", headerFont));
        if (confirmation.getDeliveryAddress() != null) {
            document.add(new Paragraph("Address: " + confirmation.getDeliveryAddress(), normalFont));
        }
        if (confirmation.getEstimatedDeliveryDate() != null) {
            document.add(new Paragraph("Estimated Delivery: " +
                    confirmation.getEstimatedDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), normalFont));
        }
        document.add(new Paragraph(" "));

        if (confirmation.getErpReferenceNumber() != null && !confirmation.getErpReferenceNumber().isEmpty()) {
            document.add(new Paragraph("ERP Reference: " + confirmation.getErpReferenceNumber(), normalFont));
        }

        if (confirmation.getAdditionalNotes() != null && !confirmation.getAdditionalNotes().isEmpty()) {
            document.add(new Paragraph("Notes: " + confirmation.getAdditionalNotes(), normalFont));
        }

        document.close();
        return baos.toByteArray();
    }

    private String createWhatsAppConfirmationMessage(OrderConfirmation confirmation) {
        StringBuilder message = new StringBuilder();
        message.append("*Order Confirmation #").append(confirmation.getConfirmationId()).append("*\n\n");
        message.append("Thank you for your order #").append(confirmation.getOrderId()).append("!\n\n");

        message.append("*Items:*\n");
        for (int i = 0; i < confirmation.getItems().size(); i++) {
            var item = confirmation.getItems().get(i);
            message.append("- ").append(item.getProductName()).append(": ")
                    .append(item.getQuantity()).append(" ")
                    .append(item.getUnit() != null ? item.getUnit() : "pcs").append("\n");
        }
        message.append("\n");

        if (confirmation.getEstimatedDeliveryDate() != null) {
            message.append("*Estimated Delivery:* ")
                    .append(confirmation.getEstimatedDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .append("\n\n");
        }

        message.append("Your order has been successfully processed!");

        return message.toString();
    }
}
