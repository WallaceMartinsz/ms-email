CREATE TABLE tb_role (
    role_id   BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE tb_user (
    user_id  BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tb_users_roles (
    user_id BIGINT NOT NULL REFERENCES tb_user (user_id),
    role_id BIGINT NOT NULL REFERENCES tb_role (role_id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE tb_email (
    email_id        BIGSERIAL PRIMARY KEY,
    owner_ref       VARCHAR(255) NOT NULL,
    email_from      VARCHAR(255) NOT NULL,
    email_to        VARCHAR(255) NOT NULL,
    subject         VARCHAR(255) NOT NULL,
    text            TEXT         NOT NULL,
    status_email    VARCHAR(20)  NOT NULL,
    error_message   TEXT,
    created_at      TIMESTAMP    NOT NULL,
    send_date_email TIMESTAMP
);

INSERT INTO tb_role (role_name) VALUES ('ROLE_ADM'), ('ROLE_USER');
