-- !Ups

INSERT INTO categories (id, name)
VALUES (1, 'League of Legends'),
       (2, 'Fortnite');

INSERT INTO invoices (id, order_id, payment_due)
VALUES (1, 1, '24-05-2020'),
       (2, 3, '30-05-2020');

INSERT INTO products (id, name, description, category_id, price)
VALUES (1, 'Blood Moon Aatrox', 'Includes: Voiceover, Additional Quotes, Animation, Visual Effects', 1, 9.99),
       (2, 'Imperial Stormtrooper', 'Original Trilogy - Star Wars', 2, 17.12),
       (3, 'Star Guardian Ahri', 'Includes: Voiceover, Animation, Visual Effects and Chromas', 1, 150.00),
       (4, 'Cuddle Team Leader', 'Royale Hearts - Legendary', 2, 10.00);

INSERT INTO promotions (id, name, flag_active, product_id, percentage_sale)
VALUES (1, 'promo_act_1', 1, 1, 50),
       (2, 'promo_inact_2', 0, 2, 20);

INSERT INTO reviews (id, product_id, description)
VALUES (1, 1, 'Pretty good for that price.'),
       (2, 1, 'Not impressive. Meh.'),
       (3, 2, 'May the 4th be with u'),
       (4, 2, 'COOL!');

INSERT INTO stock (id, product_id, quantity)
VALUES (1, 1, 10),
       (2, 3, 15),
       (3, 2, 2),
       (4, 4, 0);
