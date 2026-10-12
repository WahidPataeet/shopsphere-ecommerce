
CREATE TABLE order_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by VARCHAR(150) NOT NULL,
    reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_order_status_history
        PRIMARY KEY (id),

    CONSTRAINT fk_order_status_history_order
        FOREIGN KEY (order_id) REFERENCES orders(id),

    INDEX idx_order_status_history_order_created
        (order_id, created_at)
);
