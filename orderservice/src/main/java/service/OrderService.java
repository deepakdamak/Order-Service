package service;

import dto.OrderRequest;
import dto.OrderResponse;
import entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request, String userEmail);
    Page<Order> getOrders(Pageable pageable);
}
