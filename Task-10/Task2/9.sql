SELECT DISTINCT maker
FROM product
WHERE type='PC' AND model IN (
   SELECT model
   FROM pc
   WHERE speed>=450
);