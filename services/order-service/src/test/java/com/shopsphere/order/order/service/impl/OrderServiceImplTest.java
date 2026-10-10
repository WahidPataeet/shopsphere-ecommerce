package com.shopsphere.order.order.service.impl;


import com.shopsphere.order.order.dto.request.CreateOrderItemRequest;
import com.shopsphere.order.order.dto.request.CreateOrderRequest;
import com.shopsphere.order.order.dto.response.OrderResponse;
import com.shopsphere.order.order.entity.Order;
import com.shopsphere.order.order.entity.OrderStatus;
import com.shopsphere.order.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderAndCalculateTotals() {

        CreateOrderItemRequest item =
                new CreateOrderItemRequest(
                        10L,
                        101L,
                        "Demo Phone",
                        "PHONE-001",
                        new BigDecimal("500.00"),
                        2
                );

        CreateOrderRequest request =
                new CreateOrderRequest(
                        1L,
                        "Test Customer",
                        "9999999999",
                        "10 Main Road",
                        null,
                        "Pune",
                        "Maharashtra",
                        "411001",
                        "India",
                        List.of(item),
                        new BigDecimal("50.00"),
                        new BigDecimal("100.00")
                );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        OrderResponse response = orderService.createOrder(request);

        assertEquals(1L, response.id());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(new BigDecimal("1000.00"), response.subtotal());
        assertEquals(new BigDecimal("50.00"), response.shippingFee());
        assertEquals(new BigDecimal("100.00"), response.discountAmount());
        assertEquals(new BigDecimal("950.00"), response.totalAmount());

        assertEquals(1, response.items().size());
        assertEquals(new BigDecimal("1000.00"),
                response.items().get(0).lineTotal());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldRejectDiscountGreaterThanSubtotal() {

        CreateOrderItemRequest item =
                new CreateOrderItemRequest(
                        10L,
                        101L,
                        "Demo Phone",
                        "PHONE-001",
                        new BigDecimal("100.00"),
                        1
                );

        CreateOrderRequest request =
                new CreateOrderRequest(
                        1L,
                        "Test Customer",
                        "9999999999",
                        "10 Main Road",
                        null,
                        "Pune",
                        "Maharashtra",
                        "411001",
                        "India",
                        List.of(item),
                        BigDecimal.ZERO,
                        new BigDecimal("150.00")
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }
}