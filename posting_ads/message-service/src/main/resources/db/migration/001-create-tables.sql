CREATE SCHEMA IF NOT EXISTS message_service;

SET search_path TO message_service;

CREATE TABLE dialogs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    user1_id UUID NOT NULL,
    user2_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    dialog_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    text TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT now(),
    is_read BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_messages_dialog_id FOREIGN KEY (dialog_id) REFERENCES dialogs (id) ON DELETE CASCADE
    CONSTRAINT chk_text_length CHECK (length(text) <= 5000)
);