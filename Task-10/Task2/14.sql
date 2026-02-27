SELECT speed, avg(price::NUMERIC)
FROM pc
GROUP BY speed;