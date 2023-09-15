-- DROP TABLE IF EXISTS TB_ARTICLE;

CREATE TABLE IF NOT EXISTS  TB_ARTICLE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    body VARCHAR(2000),
    author_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 01', 'blabla 01',  1, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 02', 'blabla 02',  2, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 03', 'blabla 03',  3, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 04', 'blabla 04',  4, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 05', 'blabla 05',  5, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 06', 'blabla 06',  6, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 07', 'blabla 07',  7, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 08', 'blabla 08',  8, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 09', 'blabla 09',  9, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 10', 'blabla 10', 10, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 11', 'blabla 11', 11, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 12', 'blabla 12', 12, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 13', 'blabla 13', 13, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 14', 'blabla 14', 14, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 15', 'blabla 15', 15, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 16', 'blabla 16', 16, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 17', 'blabla 17', 17, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 18', 'blabla 18', 18, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 19', 'blabla 19', 19, current_timestamp(), current_timestamp() );
INSERT INTO TB_ARTICLE (title, body, author_id, created_at, updated_at) VALUES ( 'title 20', 'blabla 20', 20, current_timestamp(), current_timestamp() );

