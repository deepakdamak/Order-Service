package com.order.orderservice.service;

import com.order.orderservice.dto.OrderRequest;
import com.order.orderservice.dto.OrderResponse;
import com.order.orderservice.entity.Order;
import com.order.orderservice.entity.Product;
import com.order.orderservice.exception.InsufficientStockException;
import com.order.orderservice.exception.ResourceNotFoundException;
import com.order.orderservice.repository.OrderRepository;
import com.order.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String userEmail) {
        log.info("Starting order placement for product: {}, user: {}",
                request.getProductId(), userEmail);
        Product product = productRepository.findByIdForUpdate(request.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", request.getProductId());
                    return new ResourceNotFoundException(
                            "Product not found with ID: " + request.getProductId());
                });

        log.debug("Product found: {} (Stock: {})", product.getName(), product.getStock());
        if (product.getStock() < request.getQuantity()) {
            log.error("Insufficient stock. Product: {}, Requested: {}, Available: {}",
                    product.getName(), request.getQuantity(), product.getStock());
            throw new InsufficientStockException(
                    String.format("Insufficient stock for product '%s'. " +
                                    "Requested: %d, Available: %d",
                            product.getName(), request.getQuantity(), product.getStock()));
        }


        int newStock = product.getStock() - request.getQuantity();
        product.setStock(newStock);
        productRepository.save(product);
        log.info("Stock updated for product {}: {} -> {}",
                product.getId(), product.getStock(), newStock);


        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));
        Order order = new Order();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setUserEmail(userEmail);
        order.setStatus("CONFIRMED");
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return buildOrderResponse(savedOrder, product);
    }

    private OrderResponse buildOrderResponse(Order order, Product product) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setProductId(order.getProductId());
        response.setProductName(order.getProductName());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setUserEmail(order.getUserEmail());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getCreatedAt());
        response.setMessage(String.format(
                "Order for '%s' (Quantity: %d) placed successfully. Total: ₹%s",
                order.getProductName(), order.getQuantity(), order.getTotalPrice()));

        return response;
    }
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(Pageable pageable) {
        log.info("Fetching all orders with pagination: {}", pageable);
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        return ordersPage.map(this::convertToOrderResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(String userEmail, Pageable pageable) {
        log.info("Fetching orders for user: {}", userEmail);

        Page<Order> ordersPage = orderRepository.findAllByUserEmail(userEmail, pageable);

        return ordersPage.map(this::convertToOrderResponse);
    }

    private OrderResponse convertToOrderResponse(Order order) {
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
        response.setMessage("Order retrieved successfully");
        return response;
    }
    }