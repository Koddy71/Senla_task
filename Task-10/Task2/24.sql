SELECT model, price
FROM (
   SELECT model, price FROM pc
   UNION ALL
   SELECT model, price FROM laptop
   UNION ALL
   SELECT model, price FROM printer
) 
WHERE price = (
   SELECT MAX(price)
   FROM (
      SELECT price FROM pc
      UNION ALL
      SELECT price FROM laptop
      UNION ALL
      SELECT price FROM printer
   ) prices
   WHERE price IS NOT NULL
)
AND price IS NOT NULL;