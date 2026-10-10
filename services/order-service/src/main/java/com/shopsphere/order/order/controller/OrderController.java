package com.shopsphere.order.order.controller;

import com.shopsphere.order.order.dto.request.CreateOrderRequest;
import com.shopsphere.order.order.dto.response.OrderResponse;
import com.shopsphere.order.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable @Positive Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrderById(orderId));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(
                orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(
            @PathVariable @Positive Long userId) {

        return ResponseEntity.ok(
                orderService.getOrdersByUserId(userId));
    }
}
