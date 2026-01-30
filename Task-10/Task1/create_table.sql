CREATE TYPE product_type AS ENUM('PC', 'Laptop', 'Printer');

CREATE TABLE IF NOT EXISTS product (
   maker VARCHAR(10) NOT NULL,
   model VARCHAR(50) PRIMARY KEY,
   type product_type NOT NULL
);

CREATE TABLE IF NOT EXISTS pc (
   code INT PRIMARY KEY,
   model VARCHAR(50) NOT NULL,
   speed SMALLINT NOT NULL,
   ram SMALLINT NOT NULL,
   hd REAL NOT NULL,
   cd VARCHAR(10) NOT NULL,
   price MONEY,
   CONSTRAINT fk_pc_model
      FOREIGN KEY (model) REFERENCES product (model)
);

CREATE TABLE IF NOT EXISTS laptop (
   code INT PRIMARY KEY,
   model VARCHAR(50) NOT NULL,
   speed SMALLINT NOT NULL,
   ram SMALLINT NOT NULL,
   hd REAL NOT NULL,
   screen SMALLINT NOT NULL,
   price MONEY, 
   CONSTRAINT fk_laptop_model
      FOREIGN KEY (model) REFERENCES product (model)
);

CREATE TABLE IF NOT EXISTS printer (
   code INT PRIMARY KEY,
   model VARCHAR(50) NOT NULL,
   color CHAR(1) NOT NULL,
   type VARCHAR(10) NOT NULL,
   price MONEY,
   CONSTRAINT fk_printer_model
      FOREIGN KEY (model) REFERENCES product (model)
);

