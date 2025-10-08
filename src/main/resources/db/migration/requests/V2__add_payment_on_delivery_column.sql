-- =====================================================
-- BC: Requests — Add column payment_on_delivery
-- =====================================================

ALTER TABLE requests.requests
    ADD COLUMN payment_on_delivery BOOLEAN NOT NULL DEFAULT FALSE;
