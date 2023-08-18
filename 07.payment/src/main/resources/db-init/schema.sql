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
    `name` VARCHAR(200),
    local_name VARCHAR(200),
    price BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);