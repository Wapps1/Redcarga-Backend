ALTER TABLE tracking.quote_current_location
DROP CONSTRAINT IF EXISTS quote_current_location_pkey;

ALTER TABLE tracking.quote_current_location
    ADD COLUMN IF NOT EXISTS current_location_id INTEGER;

ALTER TABLE tracking.quote_current_location
    ADD CONSTRAINT quote_current_location_pkey
        PRIMARY KEY (current_location_id);