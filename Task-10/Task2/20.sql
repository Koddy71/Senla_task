SELECT maker, COUNT(model) as model_count
FROM product
WHERE type = 'PC'
GROUP BY maker
HAVING COUNT(model) >= 3;