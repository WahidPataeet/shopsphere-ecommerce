package com.shopsphere.cart.cart.service;

import com.shopsphere.cart.cart.dto.AddCartItemRequest;
import com.shopsphere.cart.cart.entity.Cart;
import com.shopsphere.cart.cart.entity.CartItem;
import com.shopsphere.cart.cart.entity.CartStatus;
import com.shopsphere.cart.cart.mapper.CartMapper;
import com.shopsphere.cart.cart.repository.CartItemRepository;
import com.shopsphere.cart.cart.repository.CartRepository;
import com.shopsphere.cart.cart.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void shouldIncreaseQuantityWhenSameItemAlreadyExists() {

        Long userId = 1L;

        Cart cart = Cart.builder()
                .id(1L)
                .userId(userId)
                .status(CartStatus.ACTIVE)
                .build();

        CartItem existingItem = CartItem.builder()
                .id(10L)
                .cart(cart)
                .productId(100L)
                .variantId(200L)
                .quantity(2)
                .build();

        cart.getItems().add(existingItem);

        when(cartRepository.findByUserIdAndStatus(
                userId,
                CartStatus.ACTIVE
        )).thenReturn(Optional.of(cart));

        when(cartItemRepository
                .findByCartIdAndProductIdAndVariantId(
                        1L,
                        100L,
                        200L
                ))
                .thenReturn(Optional.of(existingItem));

        AddCartItemRequest request =
                new AddCartItemRequest(
                        100L,
                        200L,
                        3
                );

        cartService.addItem(userId, request);

        assertEquals(
                5,
                existingItem.getQuantity()
        );

        verify(cartRepository).save(cart);
    }
}
