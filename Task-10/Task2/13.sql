SELECT avg(speed)
from product p
JOIN pc ON p.model=pc.model
WHERE p.maker='A';