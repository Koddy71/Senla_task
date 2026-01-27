SELECT speed, AVG(price::numeric) as avg_price
FROM pc
WHERE speed > 600
GROUP BY speed;