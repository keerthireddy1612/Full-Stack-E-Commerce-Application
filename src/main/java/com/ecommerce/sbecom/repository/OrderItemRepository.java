package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
