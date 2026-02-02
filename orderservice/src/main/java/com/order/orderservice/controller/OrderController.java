package com.order.orderservice.controller;

import com.order.orderservice.dto.OrderRequest;
import com.order.orderservice.dto.OrderResponse;
import com.order.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        OrderResponse response = orderService.placeOrder(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            Authentication authentication) {

        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        Page<OrderResponse> orders = isAdmin
                ? orderService.getOrders(pageable)
                : orderService.getOrdersByUser(userEmail, pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            Authentication authentication) {

        String userEmail = authentication.getName();
        Page<OrderResponse> orders = orderService.getOrdersByUser(userEmail, pageable);
        return ResponseEntity.ok(orders);
    }
}