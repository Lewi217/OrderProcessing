# Sanifu-Style Order Scraping Service

A Spring Boot application that extracts orders from WhatsApp and email messages and processes them into an ERP system.

## Features

- WhatsApp business API integration for order extraction
- Email scraping for order extraction
- Rule-based order parsing (MVP phase)
- ERP integration
- Order confirmation generation and delivery
- OCR support for image-based orders (Phase 2)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Tesseract OCR (for Phase 2)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/order-scraping-service.git
   ```

2. Navigate to the project directory:
   ```bash
   cd order-scraping-service
   ```

3. Update application.properties with your configuration.

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   java -jar target/order-processing-0.0.1-SNAPSHOT.jar
   ```

## Project Structure

The project follows a standard Spring Boot structure:

- `com.sanifu.orderprocessing.controller`: REST endpoints
- `com.sanifu.orderprocessing.model`: Data models
- `com.sanifu.orderprocessing.repository`: Database access
- `com.sanifu.orderprocessing.service`: Business logic
- `com.sanifu.orderprocessing.exception`: Exception handling

## API Documentation

### WhatsApp Webhook

- `GET /api/webhook/whatsapp` - Verify webhook
- `POST /api/webhook/whatsapp` - Receive WhatsApp messages

### Email Webhook

- `POST /api/webhook/email` - Receive email messages

### Order Management

- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders/{id}/reprocess` - Reprocess an order

## Development Roadmap

### Phase 1: Core Scraping MVP

- Basic WhatsApp and email integration
- Rule-based parsing
- ERP push integration
- Order confirmation

### Phase 2: NLP and OCR Improvements

- OCR for image-based orders
- NLP for complex data extraction
- Performance and scalability improvements

### Phase 3: Full Automation & Refinements

- End-to-end automation
- User feedback loops
- Continuous improvement

