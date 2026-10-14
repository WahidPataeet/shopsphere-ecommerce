CREATE TABLE payment_webhook_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    provider VARCHAR(50) NOT NULL,
    provider_event_id VARCHAR(150) NOT NULL,
    payment_reference VARCHAR(50) NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    processing_status VARCHAR(20) NOT NULL DEFAULT 'PROCESSED',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_payment_webhook_events PRIMARY KEY (id),

    CONSTRAINT uk_webhook_provider_event
        UNIQUE (provider, provider_event_id),

    CONSTRAINT chk_webhook_processing_status
        CHECK (processing_status IN ('PROCESSED')),

    INDEX idx_webhook_payment_reference (payment_reference),
    INDEX idx_webhook_created_at (created_at)
);
