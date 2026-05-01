-- Создаём схему
CREATE SCHEMA IF NOT EXISTS user_service;

-- Таблица пользователей 
CREATE TABLE IF NOT EXISTS user_service.user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    login VARCHAR(64) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица платежей 
CREATE TABLE IF NOT EXISTS user_service.payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    user_id UUID NOT NULL,
    ad_id UUID NOT NULL,    -- логическая ссылка на Ad Service (другая БД)
    hours INTEGER NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES user_service.user (id) ON DELETE RESTRICT
);