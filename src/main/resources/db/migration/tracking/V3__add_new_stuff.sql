-- 1) Eliminar PK actual si existe
ALTER TABLE tracking.quote_current_location
    DROP CONSTRAINT IF EXISTS quote_current_location_pkey;

-- 2) Agregar columna identity (si no existe)
ALTER TABLE tracking.quote_current_location
    ADD COLUMN IF NOT EXISTS current_location_id
        INT GENERATED ALWAYS AS IDENTITY;

-- 3) Agregar velocidad
ALTER TABLE tracking.quote_current_location
    ADD COLUMN IF NOT EXISTS speed DOUBLE PRECISION;

-- 5) Volver a colocar la PRIMARY KEY
ALTER TABLE tracking.quote_current_location
    ADD CONSTRAINT quote_current_location_pkey
        PRIMARY KEY (current_location_id);
