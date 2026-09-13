CREATE TABLE product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    category_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_product PRIMARY KEY (id),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES category(id),

    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id)
        REFERENCES brand(id)
);

CREATE TABLE product_variant (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL,
    original_price DECIMAL(12,2) NOT NULL,
    selling_price DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_product_variant PRIMARY KEY (id),

    CONSTRAINT uk_product_variant_sku
        UNIQUE (sku),

    CONSTRAINT fk_variant_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
);