SELECT model, speed, hd
FROM PC
WHERE
    cd IN ('12x', '24x')
    AND CAST(price AS numeric) < 600;