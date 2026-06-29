CREATE SCHEMA IF NOT EXISTS ad_service;

-- Объявления
CREATE TABLE ad_service.ad (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    seller_id UUID NOT NULL, -- ссылка на user_service.user
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SOLD, ARCHIVED
    promoted_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Покупка (один к одному с ad)
CREATE TABLE ad_service.purchase (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    ad_id UUID NOT NULL UNIQUE, -- ссылка на ad.id
    buyer_id UUID NOT NULL, -- ссылка на user_service.user
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, CANCELLED
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    score INT CHECK (score BETWEEN 1 AND 5), -- NULL = отзыв ещё не оставлен
    comment TEXT,
    review_created_at TIMESTAMP, 
    CONSTRAINT fk_purchase_ad FOREIGN KEY (ad_id) REFERENCES ad_service.ad (id) ON DELETE RESTRICT
);