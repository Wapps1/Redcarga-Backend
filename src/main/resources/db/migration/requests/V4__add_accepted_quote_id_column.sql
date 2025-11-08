ALTER TABLE requests.requests
    ADD COLUMN IF NOT EXISTS accepted_quote_id INT;

CREATE INDEX IF NOT EXISTS IX_requests_accepted_quote
    ON requests.requests (accepted_quote_id);