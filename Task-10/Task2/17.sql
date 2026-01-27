SELECT p.type, l.model, l.speed
FROM laptop l
JOIN product p ON p.model=l.model
WHERE l.speed < ALL(SELECT speed FROM pc);