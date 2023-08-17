CREATE TABLE IF NOT EXISTS TB_ORDER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    prod_id BIGINT,
    description VARCHAR(2000),
    amount  BIGINT,
    txid VARCHAR(100),
    status VARCHAR(20),
    payment_order_id VARCHAR(37),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS TB_PROD (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(500),
    price BIGINT,
    hashtag VARCHAR(2000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS TB_PURCHASE_HISTORY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    prod_id BIGINT,
    description VARCHAR(2000),
    amount  BIGINT,
    order_id BIGINT
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);