package com.aug.main.dto;

import com.aug.main.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private List<OrderItemDto> orderItems;
}
