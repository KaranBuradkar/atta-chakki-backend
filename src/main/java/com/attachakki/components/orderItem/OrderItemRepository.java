package com.attachakki.components.orderItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  Optional<OrderItem> findByName(String name);
}