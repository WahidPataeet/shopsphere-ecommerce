package com.shopsphere.cart.cart.service.impl;

import com.shopsphere.cart.cart.client.product.ProductClient;
import com.shopsphere.cart.cart.client.product.dto.CartProductValidationResponse;
import com.shopsphere.cart.cart.dto.AddCartItemRequest;
import com.shopsphere.cart.cart.dto.CartResponse;
import com.shopsphere.cart.cart.dto.UpdateCartItemRequest;
import com.shopsphere.cart.cart.entity.Cart;
import com.shopsphere.cart.cart.entity.CartItem;
import com.shopsphere.cart.cart.entity.CartStatus;
import com.shopsphere.cart.cart.mapper.CartMapper;
import com.shopsphere.cart.cart.repository.CartItemRepository;
import com.shopsphere.cart.cart.repository.CartRepository;
import com.shopsphere.cart.cart.service.CartService;
import com.shopsphere.cart.exception.BusinessException;
import com.shopsphere.cart.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    @Override
    @Transactional
    public CartResponse getCart(Long userId) {

        Cart cart = cartRepository
                .findByUserIdAndStatus(
                        userId,
                        CartStatus.ACTIVE
                )
                .orElseGet(() -> createCart(userId));

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(
            Long userId,
            AddCartItemRequest request) {

        CartProductValidationResponse product =
                productClient.validateProductForCart(
                        request.productId(),
                        request.variantId()
                );

        validateProductForCart(product);

        Cart cart =
                getOrCreateActiveCart(userId);

        CartItem existingItem =
                cartItemRepository
                        .findByCartIdAndProductIdAndVariantId(
                                cart.getId(),
                                request.productId(),
                                request.variantId()
                        )
                        .orElse(null);

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity()
                            + request.quantity()
            );

        } else {

            CartItem newItem =
                    CartItem.builder()
                            .cart(cart)
                            .productId(request.productId())
                            .variantId(request.variantId())
                            .quantity(request.quantity())
                            .build();

            cart.addItem(newItem);
        }

        Cart savedCart =
                cartRepository.save(cart);

        return cartMapper.toResponse(savedCart);
    }


    @Override
    public CartResponse updateItem(
            Long userId,
            Long itemId,
            UpdateCartItemRequest request) {

        Cart cart = getActiveCart(userId);

        CartItem item = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: " + itemId
                        )
                );

        if (!item.getCart().getId().equals(cart.getId())) {

            throw new BusinessException(
                    "Cart item does not belong to your cart"
            );
        }

        item.setQuantity(request.quantity());

        return cartMapper.toResponse(cart);
    }

    @Override
    public void removeItem(
            Long userId,
            Long itemId) {

        Cart cart = getActiveCart(userId);

        CartItem item = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: " + itemId
                        )
                );

        if (!item.getCart().getId().equals(cart.getId())) {

            throw new BusinessException(
                    "Cart item does not belong to your cart"
            );
        }

        cart.removeItem(item);
    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = getActiveCart(userId);

        cart.getItems().clear();
    }

    private Cart getActiveCart(Long userId) {

        return cartRepository
                .findByUserIdAndStatus(
                        userId,
                        CartStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active cart not found"
                        )
                );
    }

    private Cart getOrCreateActiveCart(Long userId) {

        return cartRepository
                .findByUserIdAndStatus(
                        userId,
                        CartStatus.ACTIVE
                )
                .orElseGet(() -> createCart(userId));
    }

    private Cart createCart(Long userId) {

        Cart cart = Cart.builder()
                .userId(userId)
                .status(CartStatus.ACTIVE)
                .build();

        return cartRepository.save(cart);
    }

    private void validateProductForCart(
            CartProductValidationResponse product) {

        if (!product.productActive()) {

            throw new BusinessException(
                    "Product is not active"
            );
        }

        if (!product.variantActive()) {

            throw new BusinessException(
                    "Product variant is not active"
            );
        }

        if (!product.variantBelongsToProduct()) {

            throw new BusinessException(
                    "Variant does not belong to the selected product"
            );
        }
    }
}
