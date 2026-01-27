SELECT DISTINCT maker
FROM product
WHERE type = 'Printer' AND maker IN (
   SELECT maker
   FROM product p
   JOIN pc ON p.model = pc.model
   WHERE pc.ram = (SELECT MIN(ram) FROM pc)
      AND pc.speed = (
         SELECT MAX(speed)
         FROM pc
         WHERE ram = (SELECT MIN(ram) FROM pc)
      )
);