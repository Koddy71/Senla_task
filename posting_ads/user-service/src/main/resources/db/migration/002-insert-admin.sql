INSERT INTO
    user_service.user (
        login,
        passwordhash,
        full_name,
        phone,
        balance,
        role,
        blocked,
        created_at,
        updated_at
    )
SELECT 'admin', '$2y$10$XztvjgPD192r4gqRjVym..7C5kGRXdOccIKuHDvPO6qk138g1P3M.', 'Administrator', NULL, 0.00, 'ADMIN', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE
    NOT EXISTS (
        SELECT 1
        FROM user_service.user
        WHERE
            login = 'admin'
    );