ALTER TABLE deals.outbox
ALTER COLUMN payload TYPE text USING payload::text;