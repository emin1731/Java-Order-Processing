package com.emindabakhov.orderprocessing.service;
import com.emindabakhov.orderprocessing.dto.OrderRequest;
import com.emindabakhov.orderprocessing.dto.OrderResponse;
import com.emindabakhov.orderprocessing.entity.Order;
import com.emindabakhov.orderprocessing.entity.OrderStatus;
import com.emindabakhov.orderprocessing.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order = orderRepository.save(order);
        log.info("Order {} saved with PENDING status", order.getId());
// Publish to RabbitMQ
        rabbitTemplate.convertAndSend(exchange, routingKey, order.getId());
        log.info("Order {} published to RabbitMQ", order.getId());
        return toResponse(order);
    }
    @Cacheable(value = "orders", key = "#id")
    public OrderResponse getOrderById(Long id) {
        log.info("Cache MISS for order {} - fetching from DB", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return toResponse(order);
    }
    @Transactional
    public void markCompleted(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            log.info("Order {} marked COMPLETED", orderId);
        });
    }
    @CacheEvict(value = "orders", key = "#id")
    public void evictOrder(Long id) {
        log.info("Cache EVICTED for order {}", id);
    }
    private OrderResponse toResponse(Order o) {
        return new OrderResponse(
                o.getId(), o.getProductName(), o.getQuantity(), o.getStatus(),
                o.getCreatedAt());
    }
}