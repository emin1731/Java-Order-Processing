package com.emindabakhov.orderprocessing.consumer;

import com.emindabakhov.orderprocessing.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderService orderService;
    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void processOrder(Long orderId) {
        log.info("Received order {} from queue - processing...", orderId);
        try {
            Thread.sleep(3000); // simulate processing delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        orderService.markCompleted(orderId); // update DB
        orderService.evictOrder(orderId); // invalidate cache
        log.info("Order {} processing complete", orderId);
    }
}