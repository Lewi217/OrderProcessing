package com.sanifu.order_processing.repository;


import com.sanifu.order_processing.model.Order;
import com.sanifu.order_processing.model.OrderSource;
import com.sanifu.order_processing.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findBySourceAndSourceIdentifier(OrderSource source, String sourceIdentifier);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<Order> findByExternalId(String externalId);
}
