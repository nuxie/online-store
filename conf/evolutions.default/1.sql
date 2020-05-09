-- !Ups

CREATE TABLE "carts" --- TODO: refactor to carts / carts products
(
    "id"         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"    INT     NOT NULL,
    "product_id" INT     NOT NULL,
    "quantity"   INT     NOT NULL,
    FOREIGN KEY ("user_id") references "users" (id),
    FOREIGN KEY ("product_id") references "products" (id),
    UNIQUE (user_id) --- only one cart per user (that's what differs it from wishlist)
);

CREATE TABLE "categories"
(
    "id"   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL,
    UNIQUE (name)
);

CREATE TABLE "users" --- TODO: add address etc; for now it's just login-email-taxnum info
(
    "id"         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"       VARCHAR NOT NULL,
    "e_mail"     VARCHAR NOT NULL,
    "tax_number" INT
);

CREATE TABLE "invoices"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "order_id"    VARCHAR NOT NULL,
    "payment_due" VARCHAR NOT NULL,
    FOREIGN KEY ("order_id") references "orders" (id)
);

CREATE TABLE "orders" --- TODO: refactor to orders / orders products
(
    "id"         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product_id" INT     NOT NULL,
    "user_id"    INT     NOT NULL,
    "quantity"   INT     NOT NULL,
    "tax_number" INT,
    FOREIGN KEY ("user_id") references "users" (id),
    FOREIGN KEY ("product_id") references "products" (id),
    FOREIGN KEY ("tax_number") references "users" (tax_number)
);

CREATE TABLE "products"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"        VARCHAR NOT NULL,
    "description" VARCHAR NOT NULL,
    "price"       REAL    NOT NULL
);

CREATE TABLE "promotions"
(
    "id"              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"            VARCHAR NOT NULL,
    "flag_active"     INT     NOT NULL,
    "product_id"      INT     NOT NULL,
    "percentage_sale" INT     NOT NULL,
    FOREIGN KEY ("product_id") references "products" (id)
);

CREATE TABLE "reviews"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product_id"  INT     NOT NULL,
    "user_id"     INT     NOT NULL,
    "quantity"    INT     NOT NULL,
    "description" VARCHAR NOT NULL,
    FOREIGN KEY ("user_id") references "users" (id),
    FOREIGN KEY ("product_id") references "products" (id)
);

CREATE TABLE "stock"
(
    "id"         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product_id" INT     NOT NULL,
    "quantity"   INT     NOT NULL,
    FOREIGN KEY ("product_id") references "products" (id),
    UNIQUE (product_id) --- want to have only row for each product
);

CREATE TABLE "wishlists"
(
    "id"         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"    INT     NOT NULL,
    "product_id" INT     NOT NULL,
    "quantity"   INT     NOT NULL,
    FOREIGN KEY ("user_id") references "users" (id),
    FOREIGN KEY ("product_id") references "products" (id)
);

-- !Downs

DROP TABLE "carts";
DROP TABLE "categories";
DROP TABLE "users";
DROP TABLE "invoices";
DROP TABLE "orders";
DROP TABLE "products";
DROP TABLE "promotions";
DROP TABLE "reviews";
DROP TABLE "stock";
DROP TABLE "wishlists";