CREATE TABLE inventory (
    id BIGINT NOT NULL AUTO_INCREMENT,

    variant_id BIGINT NOT NULL,

    total_stock INT NOT NULL DEFAULT 0,

    reserved_stock INT NOT NULL DEFAULT 0,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_inventory
        PRIMARY KEY (id),

    CONSTRAINT uk_inventory_variant
        UNIQUE (variant_id),

    CONSTRAINT chk_inventory_total_stock
        CHECK (total_stock >= 0),

    CONSTRAINT chk_inventory_reserved_stock
        CHECK (reserved_stock >= 0),

    CONSTRAINT chk_inventory_reserved_not_greater_than_total
        CHECK (reserved_stock <= total_stock)
);