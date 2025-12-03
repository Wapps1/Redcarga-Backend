ALTER TABLE tracking.quote_current_location
DROP CONSTRAINT IF EXISTS quote_current_location_pkey;

ALTER TABLE tracking.quote_current_location
    ALTER COLUMN current_location_id DROP DEFAULT;

ALTER TABLE tracking.quote_current_location
ALTER COLUMN current_location_id ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE tracking.quote_current_location
    ADD CONSTRAINT quote_current_location_pkey
        PRIMARY KEY (current_location_id);
