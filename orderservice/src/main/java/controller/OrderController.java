package controller;

import dto.OrderRequest;
import dto.OrderResponse;
import entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request,
                                                    Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(orderService.placeOrder(request, userEmail));
    }


    @GetMapping
    public Page<Order> getOrders(Pageable pageable) {
        return orderService.getOrders(pageable);
    }
}
