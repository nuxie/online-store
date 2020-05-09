-- !Ups
INSERT INTO carts (id, user_id, product_id, quantity)
VALUES (1, 1, 1, 2),
       (2, 1, 2, 1);

INSERT INTO categories (id, name)
VALUES (1, 'A'),
       (2, 'B');

INSERT INTO users (id, name, e_mail, tax_number)
VALUES (1, 'userA', 'A@email.com'),
       (2, 'userB', 'B@email.com', '123456');

INSERT INTO invoices (id, order_id, payment_due)
VALUES (1, 1, '24-05-2020'),
       (2, 3, '30-05-2020');

INSERT INTO orders (id, order_id, product_id, user_id, quantity)
VALUES (1, 1, 1, 2, 1),
       (2, 1, 2, 2, 2),
       (3, 2, 1, 1, 1),
       (4, 3, 2, 2, 3),
       (5, 3, 1, 2, 2),
       (6, 3, 3, 2,4);

INSERT INTO products (id, name, description, price)
VALUES (1, 'Product1', 'Desc1', 9.99),
       (2, 'Product2', 'Desc2', 17.12),
       (3, 'Product3', 'Desc3', 150.00);

INSERT INTO promotions (id, name, flag_active, product_id, percentage_sale)
VALUES (1, 'promo_act_1', 1, 19.99, 50),
       (2, 'promo_inact_2', 0, 2, 20);

INSERT INTO reviews (id, product_id, description)
VALUES (1, 1, 'bad'),
       (2, 1, 'good'),
       (3, 2, 'so-so');

INSERT INTO stock (id, product_id, quantity)
VALUES (1, 1, 10),
       (2, 3, 15),
       (3, 2, 2);

INSERT INTO wishlists (id, user_id, product_id)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 2, 3);

-- !Downs

DELETE FROM carts WHERE id=1;
DELETE FROM carts WHERE id=2;
DELETE FROM categories WHERE id=1;
DELETE FROM categories WHERE id=2;
DELETE FROM users WHERE id=1;
DELETE FROM users WHERE id=2;
DELETE FROM invoices WHERE id=1;
DELETE FROM invoices WHERE id=2;
DELETE FROM orders WHERE id=1;
DELETE FROM orders WHERE id=2;
DELETE FROM orders WHERE id=3;
DELETE FROM orders WHERE id=4;
DELETE FROM orders WHERE id=5;
DELETE FROM orders WHERE id=6;
DELETE FROM products WHERE id=1;
DELETE FROM products WHERE id=2;
DELETE FROM products WHERE id=3;
DELETE FROM promotions WHERE id=1;
DELETE FROM promotions WHERE id=2;
DELETE FROM reviews WHERE id=1;
DELETE FROM reviews WHERE id=2;
DELETE FROM reviews WHERE id=3;
DELETE FROM stock WHERE id=1;
DELETE FROM stock WHERE id=2;
DELETE FROM stock WHERE id=3;
DELETE FROM wishlists WHERE id=1;
DELETE FROM wishlists WHERE id=2;
DELETE FROM wishlists WHERE id=3;