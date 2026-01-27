CREATE TABLE product (
   maker VARCHAR(10) NOT NULL,
   model VARCHAR(50) PRIMARY KEY,
   type VARCHAR(50) NOT NULL CHECK (type IN ('PC', 'Laptop', 'Printer'))
);

CREATE TABLE pc (
   code INT PRIMARY KEY,
   model VARCHAR(50) NOT NULL,
   speed SMALLINT NOT NULL,
   ram SMALLINT NOT NULL,
   hd REAL NOT NULL,
   cd VARCHAR(10) NOT NULL,
   price MONEY,
   FOREIGN KEY (model) REFERENCES product (model)
);

CREATE TABLE laptop (
   code INT PRIMARY KEY,
   model VARCHAR(50) NOT NULL,
   speed SMALLINT NOT NULL,
   ram SMALLINT NOT NULL,
   hd REAL NOT NULL,
   screen SMALLINT NOT NULL,
   price MONEY, 
   FOREIGN KEY (model) REFERENCES product (model)
);

CREATE TABLE printer (
   code INT PRIMARY KEY,
   model VARCHAR(50) NOT NULL,
   color CHAR(1) NOT NULL,
   type VARCHAR(10) NOT NULL,
   price MONEY,
   FOREIGN KEY (model) REFERENCES product (model)
);

