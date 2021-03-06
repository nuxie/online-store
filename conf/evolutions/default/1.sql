-- !Ups

CREATE TABLE USERS (
    USER_ID      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    FIRST_NAME   TEXT,
    LAST_NAME    TEXT,
    EMAIL        TEXT,
    PROVIDER_ID  TEXT NOT NULL,
    PROVIDER_KEY TEXT NOT NULL
);

CREATE TABLE PASSWORDS (
   PROVIDER_KEY VARCHAR(254) NOT NULL PRIMARY KEY,
   HASHER   TEXT         NOT NULL,
   HASH     TEXT         NOT NULL,
   SALT     TEXT
);

CREATE TABLE CATEGORIES
(
    ID   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    NAME VARCHAR NOT NULL,
    UNIQUE (NAME)
);

CREATE TABLE PRODUCTS
(
    ID          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    NAME        VARCHAR NOT NULL,
    DESCRIPTION VARCHAR NOT NULL,
    CATEGORY_ID    INT     NOT NULL,
    PRICE       REAL    NOT NULL,
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES (ID)
);

CREATE TABLE CARTS
(
    ID         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    USER_ID    TEXT NOT NULL,
    PRODUCT_ID INT     NOT NULL,
    QUANTITY   INT     NOT NULL,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID)
);

CREATE TABLE ORDERS_PRODUCTS
(
    ID         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    ORDER_ID    INT     NOT NULL,
    PRODUCT_ID INT     NOT NULL,
    QUANTITY   INT     NOT NULL,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID),
    FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID)
);

CREATE TABLE ORDERS
(
    ID         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    USER_ID    TEXT NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES APPUSER (ID)
);

CREATE TABLE INVOICES
(
    ID          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    ORDER_ID    INT NOT NULL,
    PAYMENT_DUE VARCHAR NOT NULL,
    FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID)
);

CREATE TABLE PROMOTIONS
(
    ID              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    NAME            VARCHAR NOT NULL,
    FLAG_ACTIVE     INT     NOT NULL,
    PRODUCT_ID      INT     NOT NULL,
    PERCENTAGE_SALE INT     NOT NULL,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID)
);

CREATE TABLE REVIEWS
(
    ID          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    PRODUCT_ID  INT     NOT NULL,
    DESCRIPTION VARCHAR NOT NULL,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID)
);

CREATE TABLE stock
(
    ID         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    PRODUCT_ID INT     NOT NULL,
    QUANTITY   INT     NOT NULL,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID),
    UNIQUE (PRODUCT_ID)
);

CREATE TABLE WISHLISTS
(
    ID         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    USER_ID    INT     NOT NULL,
    PRODUCT_ID INT     NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES APPUSER (ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID)
);

CREATE TABLE APPUSER (
    ID	        TEXT NOT NULL UNIQUE PRIMARY KEY,
    EMAIL	    TEXT NOT NULL,
    FIRST_NAME	TEXT,
    LAST_NAME	TEXT,
    ROLE	    TEXT NOT NULL
);

CREATE TABLE LOGININFO (
    ID	            TEXT NOT NULL UNIQUE,
    PROVIDER_ID	    TEXT NOT NULL,
    PROVIDER_KEY	TEXT NOT NULL
);

CREATE TABLE USERLOGININFO (
     USER_ID	    TEXT NOT NULL,
     LOGININFO_ID	TEXT NOT NULL,
     FOREIGN KEY(USER_ID) REFERENCES APPUSER(ID),
     FOREIGN KEY(LOGININFO_ID) REFERENCES LOGININFO(ID)
);


CREATE TABLE PASSWORDINFO (
    HASHER	        TEXT NOT NULL,
    PASSWORD	    TEXT NOT NULL,
    SALT	        TEXT,
    LOGININFO_ID	TEXT NOT NULL,
    FOREIGN KEY(LOGININFO_ID) REFERENCES LOGININFO(ID)
);

CREATE TABLE OAUTH2INFO (
    ID	            TEXT NOT NULL UNIQUE PRIMARY KEY,
    ACCESS_TOKEN	TEXT NOT NULL,
    TOKEN_TYPE	    TEXT,
    EXPIRES_IN	    INTEGER,
    REFRESH_TOKEN	TEXT,
    LOGININFO_ID	TEXT NOT NULL,
    FOREIGN KEY(LOGININFO_ID) REFERENCES LOGININFO(ID)
);

-- !Downs

DROP TABLE CARTS;
DROP TABLE CATEGORIES;
DROP TABLE USERS;
DROP TABLE INVOICES;
DROP TABLE ORDERS;
DROP TABLE ORDERS_PRODUCTS;
DROP TABLE PRODUCTS;
DROP TABLE PROMOTIONS;
DROP TABLE REVIEWS;
DROP TABLE STOCK;
DROP TABLE WISHLISTS;
DROP TABLE PASSWORDS;
DROP TABLE OAUTH2INFO;
DROP TABLE PASSWORDINFO;
DROP TABLE LOGININFO;
DROP TABLE USERLOGININFO;
DROP TABLE APPUSER;