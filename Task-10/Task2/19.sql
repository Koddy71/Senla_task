SELECT p.maker, AVG(l.screen)
FROM product p
JOIN laptop l ON p.model=l.model
WHERE p.type = 'Laptop'
GROUP BY p.maker;