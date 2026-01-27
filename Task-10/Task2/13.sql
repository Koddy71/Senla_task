SELECT avg(speed)
from product p
JOIN pc USING(model)
WHERE p.maker='A';