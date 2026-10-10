package com.shopsphere.order.order.service;

import com.shopsphere.order.order.dto.request.CreateOrderRequest;
import com.shopsphere.order.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long orderId);

    OrderResponse getOrderByNumber(String orderNumber);

    List<OrderResponse> getOrdersByUserId(Long userId);
}
