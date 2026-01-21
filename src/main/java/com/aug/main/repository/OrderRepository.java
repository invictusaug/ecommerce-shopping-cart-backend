package com.aug.main.repository;

import com.aug.main.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("""
    select o from Order o
    left join fetch o.orderItems oi
    left join fetch oi.product
    where o.id = :orderId
""")
    Optional<Order> findOrderWithItems(@Param("orderId") Long orderId);

}