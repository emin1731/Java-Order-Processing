package com.emindabakhov.orderprocessing.dto;

import com.emindabakhov.orderprocessing.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse implements Serializable {
    private Long id;
    private String productName;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
