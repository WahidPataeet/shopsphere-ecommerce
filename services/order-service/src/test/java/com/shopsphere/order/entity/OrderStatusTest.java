package com.shopsphere.order.entity;

import com.shopsphere.order.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void pendingCanBeConfirmed() {
        assertTrue(
                OrderStatus.PENDING.canTransitionTo(
                        OrderStatus.CONFIRMED
                )
        );
    }

    @Test
    void pendingCanBeCancelled() {
        assertTrue(
                OrderStatus.PENDING.canTransitionTo(
                        OrderStatus.CANCELLED
                )
        );
    }

    @Test
    void pendingCannotBeDeliveredDirectly() {
        assertFalse(
                OrderStatus.PENDING.canTransitionTo(
                        OrderStatus.DELIVERED
                )
        );
    }

    @Test
    void deliveredIsTerminal() {
        assertFalse(
                OrderStatus.DELIVERED.canTransitionTo(
                        OrderStatus.CANCELLED
                )
        );

        assertFalse(
                OrderStatus.DELIVERED.canTransitionTo(
                        OrderStatus.PROCESSING
                )
        );
    }

    @Test
    void sameStatusIsNotAValidTransition() {
        assertFalse(
                OrderStatus.PENDING.canTransitionTo(
                        OrderStatus.PENDING
                )
        );
    }

    @Test
    void failedIsTerminal() {
        assertFalse(
                OrderStatus.FAILED.canTransitionTo(
                        OrderStatus.CONFIRMED
                )
        );
    }
}
