SELECT DISTINCT maker
FROM product
WHERE maker IN (
   SELECT p.maker
   FROM product p
   JOIN pc ON p.model = pc.model
   WHERE pc.speed >= 750
) AND maker IN (
   SELECT p.maker
   FROM product p
   JOIN laptop l ON p.model = l.model
   WHERE l.speed >= 750
);