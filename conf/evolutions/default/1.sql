-- !Ups

CREATE TABLE users (
    USER_ID      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    FIRST_NAME   TEXT,
    LAST_NAME    TEXT,
    EMAIL        TEXT,
    PROVIDER_ID  TEXT NOT NULL,
    PROVIDER_KEY TEXT NOT NULL
);

CREATE TABLE passwords (
   PROVIDER_KEY VARCHAR(254) NOT NULL PRIMARY KEY,
   HASHER   TEXT         NOT NULL,
   HASH     TEXT         NOT NULL,
   SALT     TEXT
);

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

CREATE TABLE carts
(
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    user_id    TEXT NOT NULL,
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
    user_id    TEXT NOT NULL,
    FOREIGN KEY (user_id) references AppUser (Id)
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
    FOREIGN KEY (user_id) references AppUser (Id),
    FOREIGN KEY (product_id) references products (id)
);

CREATE TABLE "AppUser" (
                           "Id"	TEXT NOT NULL UNIQUE,
                           "Email"	TEXT NOT NULL,
                           "FirstName"	TEXT,
                           "LastName"	TEXT,
                           "Role"	TEXT NOT NULL,
                           PRIMARY KEY("Id")
);

CREATE TABLE "LoginInfo" (
                             "Id"	TEXT NOT NULL UNIQUE,
                             "ProviderId"	TEXT NOT NULL,
                             "ProviderKey"	TEXT NOT NULL
);

CREATE TABLE "UserLoginInfo" (
                                 "UserId"	TEXT NOT NULL,
                                 "LoginInfoId"	TEXT NOT NULL,
                                 FOREIGN KEY("UserId") REFERENCES "AppUser"("Id"),
                                 FOREIGN KEY("LoginInfoId") REFERENCES "LoginInfo"("Id")
);


CREATE TABLE "PasswordInfo" (
                                "Hasher"	TEXT NOT NULL,
                                "Password"	TEXT NOT NULL,
                                "Salt"	TEXT,
                                "LoginInfoId"	TEXT NOT NULL,
                                FOREIGN KEY("LoginInfoId") REFERENCES "LoginInfo"("Id")
);

CREATE TABLE "OAuth2Info" (
                              "Id"	TEXT NOT NULL UNIQUE,
                              "AccessToken"	TEXT NOT NULL,
                              "TokenType"	TEXT,
                              "ExpiresIn"	INTEGER,
                              "RefreshToken"	TEXT,
                              "LoginInfoId"	TEXT NOT NULL,
                              PRIMARY KEY("Id"),
                              FOREIGN KEY("LoginInfoId") REFERENCES "LoginInfo"("Id")
);

CREATE TABLE "opinion" (
                           "id"   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                           "review" VARCHAR NOT NULL,
                           "stars" INTEGER NOT NULL CHECK ("stars">=1 AND "stars" <=5),
                           "userName" VARCHAR NOT NULL ,
                           "product" INTEGER NOT NULL,
                           FOREIGN KEY (product) references products (id)
);

CREATE TABLE "favourites" (
                              "user" VARCHAR NOT NULL,
                              "product" INTEGER NOT NULL,
                              FOREIGN KEY (user) references AppUser (id),
                              FOREIGN KEY (product) references products (id)

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
DROP TABLE passwords;
DROP TABLE OAuth2Info;
DROP TABLE PasswordInfo;
DROP TABLE LoginInfo;
DROP TABLE UserLoginInfo;
DROP TABLE AppUser;
DROP TABLE opinion;
DROP TABLE favourites;
