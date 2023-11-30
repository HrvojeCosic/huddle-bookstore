CREATE TABLE books(
    id SERIAL NOT NULL PRIMARY KEY,
    title VARCHAR(250),
    type VARCHAR(50),
    base_price NUMERIC(19,3),
    count INTEGER
);

CREATE TABLE customers(
    id SERIAL NOT NULL PRIMARY KEY,
    loyalty_points INTEGER
);