INSERT INTO users (name, email)
VALUES ('Петров', 'some2@email.com'),
('Сидоров', 'sidorov@email.com'),
('Соколов', 'sokolov@email.com');

INSERT INTO requests (description, requestor_id, created)
VALUES ('Запрос на вещь', 1, '2022-12-25 23.59.59');

INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Вещь 2', 'Описание вещи 2', TRUE, 1, NULL),
('Вещь, которую брали в аренду', 'Описание вещи, которую брали в аренду', TRUE, 2, NULL),
('Вещь по запросу', 'Описание вещи по запросу', FALSE, 2, 1),
('Тестовая вещь', 'Описание вещи номер 4', TRUE, 2, NULL);

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2022-12-29 23.59.59', '2022-12-30 23.59.59', 2, 1, 1),
('2021-12-29 23.59.59', '2021-12-30 23.59.59', 4, 1, 1),
('2025-12-29 23.59.59', '2025-12-30 23.59.59', 4, 1, 0);

INSERT INTO comments (text, item_id, author_id, created)
VALUES ('Вещь понравилась, спасибо', 2, 1, '2022-12-31 23.59.59');