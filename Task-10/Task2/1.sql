SELECT model, speed, hd 
FROM pc
WHERE CAST(price AS NUMERIC) < 500 AND price IS NOT NUll;