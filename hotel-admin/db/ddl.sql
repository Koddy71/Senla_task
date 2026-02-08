DROP TABLE IF EXISTS room CASCADE;

DROP TABLE IF EXISTS service CASCADE;

DROP TABLE IF EXISTS guest CASCADE;

DROP TABLE IF EXISTS guest_service CASCADE;

CREATE TABLE room (
   number INT PRIMARY KEY NOT NULL,
   price INT NOT NULL,
   capacity INT NOT NULL,
   stars INT NOT NULL
);

CREATE TABLE service (
   id INT PRIMARY KEY NOT NULL,
   name VARCHAR(100) NOT NULL,
   price INT NOT NULL
);

CREATE TABLE guest (
   id SERIAL PRIMARY KEY NOT NULL,
   name VARCHAR(100) NOT NULL,
   roomNumber INT NOT NULL,
   checkInDate DATE NOT NULL,
   checkOutDate DATE NOT NULL,
   CONSTRAINT fk_guest_room 
      FOREIGN KEY (roomNumber) REFERENCES room (number)
);

CREATE TABLE guest_service (
    guest_id INT NOT NULL REFERENCES guest (id),
    service_id INT NOT NULL REFERENCES service (id)
);
