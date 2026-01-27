SELECT p.maker, MAX(pc.price) as max_price
FROM product p
JOIN pc ON p.model = pc.model
WHERE pc.price IS NOT NULL
GROUP BY p.maker;