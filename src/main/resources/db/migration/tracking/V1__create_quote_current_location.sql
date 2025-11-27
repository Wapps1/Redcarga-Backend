CREATE SCHEMA IF NOT EXISTS tracking;

CREATE TABLE IF NOT EXISTS tracking.quote_current_location (
    quote_id      INTEGER PRIMARY KEY,
    driver_id     INTEGER NOT NULL,
    lat           NUMERIC(9, 6) NOT NULL,
    lng           NUMERIC(9, 6) NOT NULL,

    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_quote_current_location__lat
    CHECK (lat BETWEEN -90 AND 90),

    CONSTRAINT chk_quote_current_location__lng
    CHECK (lng BETWEEN -180 AND 180)
    );
