DROP TABLE IF EXISTS guest_service CASCADE;
DROP TABLE IF EXISTS guest CASCADE;
DROP TABLE IF EXISTS service CASCADE;
DROP TABLE IF EXISTS room CASCADE;

CREATE TABLE room (
   number INT PRIMARY KEY NOT NULL,
   price INT NOT NULL CHECK (price > 0),              
   capacity INT NOT NULL CHECK (capacity >= 1),       
   stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5)   
);

CREATE TABLE service (
   id INT PRIMARY KEY NOT NULL,
   name VARCHAR(100) NOT NULL CHECK (name <> ''),     
   price INT NOT NULL CHECK (price >= 0)              
);

CREATE TABLE guest (
   id INT PRIMARY KEY NOT NULL,
   name VARCHAR(100) NOT NULL CHECK (name <> ''),     
   roomNumber INT NOT NULL,
   checkInDate DATE NOT NULL,
   checkOutDate DATE NOT NULL,
   CONSTRAINT fk_guest_room FOREIGN KEY (roomNumber) REFERENCES room (number),
   CONSTRAINT check_dates CHECK (checkOutDate > checkInDate) 
);

CREATE TABLE guest_service (
    guest_id INT NOT NULL REFERENCES guest (id),
    service_id INT NOT NULL REFERENCES service (id)
);