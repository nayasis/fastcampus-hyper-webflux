CREATE TABLE IF NOT EXISTS TB_ORDER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    prod_id BIGINT,
    prod_price BIGINT,
    bill_price BIGINT,
    txid VARCHAR(100),
    status INT,
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