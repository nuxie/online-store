-- !Ups

CREATE TABLE categories
(
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name VARCHAR NOT NULL,
    UNIQUE (name)
);

CREATE TABLE products
(
    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    category_id    INT     NOT NULL,
    price       REAL    NOT NULL,
    FOREIGN KEY (category_id) references categories (id)
);

CREATE TABLE users
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name       VARCHAR NOT NULL,
    e_mail     VARCHAR NOT NULL,
    tax_number INT
);

CREATE TABLE carts
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    user_id    INT     NOT NULL,
    product_id INT     NOT NULL,
    quantity   INT     NOT NULL,
    FOREIGN KEY (product_id) references products (id)
);

CREATE TABLE orders_products
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    order_id    INT     NOT NULL,
    product_id INT     NOT NULL,
    quantity   INT     NOT NULL,
    FOREIGN KEY (product_id) references products (id),
    FOREIGN KEY (order_id) references orders (id)
);

CREATE TABLE orders
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    user_id    INT     NOT NULL,
    FOREIGN KEY (user_id) references users (id)
);

CREATE TABLE invoices
(
    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    order_id    INT NOT NULL,
    payment_due VARCHAR NOT NULL,
    FOREIGN KEY (order_id) references orders (id)
);

CREATE TABLE promotions
(
    id              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR NOT NULL,
    flag_active     INT     NOT NULL,
    product_id      INT     NOT NULL,
    percentage_sale INT     NOT NULL,
    FOREIGN KEY (product_id) references products (id)
);

CREATE TABLE reviews
(
    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    product_id  INT     NOT NULL,
    description VARCHAR NOT NULL,
    FOREIGN KEY (product_id) references products (id)
);

CREATE TABLE stock
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    product_id INT     NOT NULL,
    quantity   INT     NOT NULL,
    FOREIGN KEY (product_id) references products (id),
    UNIQUE (product_id)
);

CREATE TABLE wishlists
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    user_id    INT     NOT NULL,
    product_id INT     NOT NULL,
    FOREIGN KEY (user_id) references users (id),
    FOREIGN KEY (product_id) references products (id)
);

-- !Downs

DROP TABLE carts;
DROP TABLE categories;
DROP TABLE users;
DROP TABLE invoices;
DROP TABLE orders;
DROP TABLE orders_products;
DROP TABLE products;
DROP TABLE promotions;
DROP TABLE reviews;
DROP TABLE stock;
DROP TABLE wishlists;