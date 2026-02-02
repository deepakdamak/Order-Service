package com.order.orderservice.repository;

import com.order.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserEmail(String userEmail);

    Page<Order> findAllByUserEmail(String userEmail, Pageable pageable);

    Page<Order> findAll(Pageable pageable);
}