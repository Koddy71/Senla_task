SELECT model, ram, screen
FROM laptop
WHERE CAST(price AS NUMERIC) > 1000 AND price IS NOT NULL;