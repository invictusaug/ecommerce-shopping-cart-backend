package com.aug.main.service.order;

import com.aug.main.dto.OrderDto;
import com.aug.main.enums.OrderStatus;
import com.aug.main.exceptions.ResourceNotFoundException;
import com.aug.main.model.Cart;
import com.aug.main.model.Order;
import com.aug.main.model.OrderItem;
import com.aug.main.model.Product;
import com.aug.main.repository.OrderRepository;
import com.aug.main.repository.ProductRepository;
import com.aug.main.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalPrice(orderItemList));

        Order save = orderRepository.save(order);
        cartService.clearCart(cart.getId());
        return save;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
           Product product = cartItem.getProduct();
           product.setInventory(product.getInventory() - cartItem.getQuantity());
           productRepository.save(product);
           return new OrderItem(
                   order,
                   product,
                   cartItem.getQuantity(),
                   cartItem.getUnitPrice()
           );
        }).toList();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItemList) {
        return orderItemList
                .stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertOrderToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository
                .findByUserId(userId)
                .stream()
                .map(this::convertOrderToDto)
                .toList();
    }

    @Override
    public OrderDto convertOrderToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
