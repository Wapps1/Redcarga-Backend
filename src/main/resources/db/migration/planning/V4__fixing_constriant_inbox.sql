ALTER TABLE planning.request_inbox
    DROP CONSTRAINT IF EXISTS chk_request_inbox_status;

ALTER TABLE planning.request_inbox
    ADD CONSTRAINT chk_request_inbox_status
    CHECK (status IN ('OPEN', 'CLOSED', 'QUOTE'));
