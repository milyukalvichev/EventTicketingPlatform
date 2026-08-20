INSERT INTO users (id, username, password, email, role)
VALUES (1, 'admin', '$2a$10$w09yC6qFqBq0mCjA8m0k0OcbL9K5m74t7vR2.T9N6B5Z1V9vE8W2S', 'admin@ticketing.com', 'ADMIN')
    ON DUPLICATE KEY UPDATE username=username;

INSERT INTO users (id, username, password, email, role)
VALUES (2, 'john_doe', '$2a$10$w09yC6qFqBq0mCjA8m0k0OcbL9K5m74t7vR2.T9N6B5Z1V9vE8W2S', 'john@example.com', 'USER')
    ON DUPLICATE KEY UPDATE username=username;

INSERT INTO venues (id, name, city, capacity)
VALUES (1, 'National Palace of Culture', 'Sofia', 3000)
    ON DUPLICATE KEY UPDATE name=name;

INSERT INTO venues (id, name, city, capacity)
VALUES (2, 'Ancient Theatre', 'Plovdiv', 2500)
    ON DUPLICATE KEY UPDATE name=name;

INSERT INTO events (id, title, base_price, event_date, venue_id)
VALUES (1, 'Rock Symphony Festival', 60.00, '2026-09-15 20:00:00', 1)
    ON DUPLICATE KEY UPDATE title=title;

INSERT INTO events (id, title, base_price, event_date, venue_id)
VALUES (2, 'Tech Innovation Summit 2026', 120.00, '2026-10-05 09:30:00', 2)
    ON DUPLICATE KEY UPDATE title=title;