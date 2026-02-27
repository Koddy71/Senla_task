SELECT model, price
FROM printer
where price = (
   SELECT max(price) 
   FROM printer
   where price is not null
);

-- Найти принтеры, имеющие самую высокую цену. Вывести поля: model, price.