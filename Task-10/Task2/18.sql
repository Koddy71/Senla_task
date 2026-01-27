SELECT DISTINCT p.maker, pr.price
FROM product p
JOIN printer pr ON p.model = pr.model
WHERE pr.color = 'y' AND pr.price = (
   SELECT MIN(price)
   FROM printer
   WHERE color = 'y'
);