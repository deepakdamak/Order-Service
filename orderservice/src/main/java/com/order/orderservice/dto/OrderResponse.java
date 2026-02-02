package com.order.orderservice.dto;

import com.order.orderservice.entity.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String userEmail;
    private String status;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private String message;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setProductId(order.getProductId());
        response.setProductName(order.getProductName());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setUserEmail(order.getUserEmail());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}