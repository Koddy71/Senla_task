SELECT p.maker, l.speed
FROM product p
JOIN laptop l ON p.model=l.model
WHERE l.hd>=100 AND l.price IS NOT NULL;