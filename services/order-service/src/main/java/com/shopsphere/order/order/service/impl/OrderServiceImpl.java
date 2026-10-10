package com.shopsphere.order.order.service.impl;

import com.shopsphere.order.exception.ResourceNotFoundException;
import com.shopsphere.order.order.dto.request.CreateOrderItemRequest;
import com.shopsphere.order.order.dto.request.CreateOrderRequest;
import com.shopsphere.order.order.dto.response.OrderItemResponse;
import com.shopsphere.order.order.dto.response.OrderResponse;
import com.shopsphere.order.order.entity.Order;
import com.shopsphere.order.order.entity.OrderItem;
import com.shopsphere.order.order.entity.OrderStatus;
import com.shopsphere.order.order.repository.OrderRepository;
import com.shopsphere.order.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        BigDecimal subtotal = request.items().stream()
                .map(item -> item.unitPrice()
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.discountAmount().compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed the order subtotal"
            );
        }

        BigDecimal total = subtotal
                .add(request.shippingFee())
                .subtract(request.discountAmount());

        Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .currency("INR")
                .subtotal(subtotal)
                .shippingFee(request.shippingFee())
                .discountAmount(request.discountAmount())
                .totalAmount(total)
                .shippingRecipient(request.shippingRecipient().trim())
                .shippingPhone(request.shippingPhone().trim())
                .shippingAddressLine1(
                        request.shippingAddressLine1().trim())
                .shippingAddressLine2(
                        normalizeOptional(request.shippingAddressLine2()))
                .shippingCity(request.shippingCity().trim())
                .shippingState(request.shippingState().trim())
                .shippingPostalCode(request.shippingPostalCode().trim())
                .shippingCountry(request.shippingCountry().trim())
                .build();

        for (CreateOrderItemRequest itemRequest : request.items()) {

            BigDecimal lineTotal = itemRequest.unitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem item = OrderItem.builder()
                    .productId(itemRequest.productId())
                    .variantId(itemRequest.variantId())
                    .productName(itemRequest.productName().trim())
                    .sku(itemRequest.sku().trim())
                    .unitPrice(itemRequest.unitPrice())
                    .quantity(itemRequest.quantity())
                    .lineTotal(lineTotal)
                    .build();

            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        ));

        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderNumber
                        ));

        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getVariantId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getLineTotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStatus(),
                order.getCurrency(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getShippingRecipient(),
                order.getShippingPhone(),
                order.getShippingAddressLine1(),
                order.getShippingAddressLine2(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
