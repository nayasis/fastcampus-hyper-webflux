DROP TABLE IF EXISTS TB_ARTICLE;

-- H2 test db 가 아닌 mariadb 를 연결하고 있을 경우에는 drop table 할 경우 flush table 도 해주어야 함 (by root)
-- FLUSH TABLES TB_ARTICLE;

CREATE TABLE TB_ARTICLE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    body VARCHAR(2000),
    author_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 1', 'blabla 01', 1234, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 2', 'blabla 02', 1234, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 3', 'blabla 03', 1234, current_timestamp(), current_timestamp() );