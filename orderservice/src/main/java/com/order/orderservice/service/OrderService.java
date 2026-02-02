package com.order.orderservice.service;

import com.order.orderservice.dto.OrderRequest;
import com.order.orderservice.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request, String userEmail);
    Page<OrderResponse> getOrders(Pageable pageable);
    Page<OrderResponse> getOrdersByUser(String userEmail, Pageable pageable);
}
