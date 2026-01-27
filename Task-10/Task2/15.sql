select hd
FROM pc
GROUP BY hd
HAVING count(hd) >= 2;