CREATE TABLE IF NOT EXISTS customer (
	Customer_ID SERIAL PRIMARY KEY NOT NULL,
	Customer_Surname varchar NOT NULL,
	Customer_Name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
	Product_ID SERIAL PRIMARY KEY NOT NULL,
	Product_Name varchar NOT NULL,
	Product_Price numeric NOT NULL
);

CREATE TABLE IF NOT EXISTS buy (
	Buy_ID SERIAL PRIMARY KEY NOT NULL,
	Buy_DateTime timestamp NOT NULL,
	Customer_ID SERIAL REFERENCES customer(Customer_ID) NOT NULL,
	Product_ID SERIAL REFERENCES products(Product_ID)NOT NULL,
	Count_product numeric NOT NULL
);


INSERT INTO customer (Customer_Surname, Customer_Name)
VALUES ('Иванов', 'Иван'),
       ('Петров', 'Антон'),
       ('Гусев', 'Ян'),
       ('Усов', 'Данил'),
       ('Павлов', 'Александр'),
	   ('Иванов', 'Сергей');


INSERT INTO products (Product_Name, Product_Price)
VALUES ('Минеральная вода', 25),
       ('Хлеб', 30),
       ('Молоко', 65),
       ('Колбаса варёная', 210),
       ('Масло', 180);

INSERT INTO buy (Buy_DateTime, Customer_ID, Product_ID, Count_product)
VALUES ('2022-10-19 10:23:54', 1, 1, 1),
	   ('2022-10-19 10:23:54', 1, 2, 1 ),
	   ('2022-10-19 10:23:54', 1, 3, 1 ),
	   ('2022-10-19 10:30:00', 2, 1, 1 ),
	   ('2022-10-19 11:00:00', 3, 4, 1 ),
	   ('2022-10-19 11:23:54', 4, 5, 2 );
