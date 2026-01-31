package com.order.orderservice.service;

import com.order.orderservice.dto.OrderRequest;
import com.order.orderservice.dto.OrderResponse;
import com.order.orderservice.entity.Order;
import com.order.orderservice.entity.Product;
import com.order.orderservice.exception.InsufficientStockException;
import com.order.orderservice.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.order.orderservice.repository.OrderRepository;
import com.order.orderservice.repository.ProductRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse placeOrder(OrderRequest request, String userEmail) {
        Product product = productRepository.findByIdForUpdate(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("Not enough stock available");
        }

        product.setStock(product.getStock() - request.getQuantity());

        Order order = new Order();
        order.setProductId(product.getId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setUserEmail(userEmail);

        orderRepository.save(order);

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setMessage("Order placed successfully");

        return response;
    }
    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

}

