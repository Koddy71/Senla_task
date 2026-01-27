SELECT model, price
FROM printer
where price = (
   SELECT max(price) 
   FROM printer
   where price is not null
);