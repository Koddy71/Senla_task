SELECT AVG(speed)
FROM laptop
where CAST(price AS NUMERIC)>1000 AND price IS NOT NULL;