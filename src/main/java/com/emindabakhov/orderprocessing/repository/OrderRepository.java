package com.emindabakhov.orderprocessing.repository;

import com.emindabakhov.orderprocessing.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
