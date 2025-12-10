package com.aug.main.service.order;

import com.aug.main.dto.OrderDto;
import com.aug.main.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertOrderToDto(Order order);
}
