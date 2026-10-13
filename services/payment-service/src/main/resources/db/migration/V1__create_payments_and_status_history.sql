CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    payment_reference VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    provider VARCHAR(50) NOT NULL DEFAULT 'MANUAL',
    provider_payment_id VARCHAR(150),
    failure_reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT uk_payments_reference UNIQUE (payment_reference),
    CONSTRAINT chk_payments_amount CHECK (amount > 0),

    INDEX idx_payments_order_created (order_id, created_at),
    INDEX idx_payments_user_created (user_id, created_at),
    INDEX idx_payments_status_created (status, created_at)
);

CREATE TABLE payment_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    previous_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_payment_status_history PRIMARY KEY (id),
    CONSTRAINT fk_payment_status_history_payment
        FOREIGN KEY (payment_id) REFERENCES payments(id),

    INDEX idx_payment_status_history_payment_created
        (payment_id, created_at)
);
