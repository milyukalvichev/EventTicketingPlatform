INSERT INTO promo_codes (code, discount_percentage, active, expiration_date)
VALUES ('SUMMER20', 20.00, true, '2026-12-31')
    ON DUPLICATE KEY UPDATE code=code;

INSERT INTO promo_codes (code, discount_percentage, active, expiration_date)
VALUES ('EARLYBIRD', 15.00, true, '2026-12-31')
    ON DUPLICATE KEY UPDATE code=code;