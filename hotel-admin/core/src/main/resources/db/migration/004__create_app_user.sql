DROP TABLE IF EXISTS app_user CASCADE;
DROP SEQUENCE IF EXISTS app_user_id_seq;

CREATE SEQUENCE IF NOT EXISTS app_user_id_seq;

CREATE TABLE app_user (
    id INT PRIMARY KEY NOT NULL DEFAULT nextval('app_user_id_seq'),
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT chk_app_user_role CHECK (role IN ('USER', 'ADMIN'))
);

ALTER SEQUENCE app_user_id_seq OWNED BY app_user.id;