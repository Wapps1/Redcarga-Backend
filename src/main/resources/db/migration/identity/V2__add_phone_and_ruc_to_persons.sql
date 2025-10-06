BEGIN;

-- Agregar columnas
ALTER TABLE identity.persons
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20),
    ADD COLUMN IF NOT EXISTS ruc   VARCHAR(11);

-- Backfill por si existen filas previas (dev/qa); ajusta si quieres otro valor
UPDATE identity.persons
SET phone = COALESCE(NULLIF(TRIM(phone), ''), '000000000')
WHERE phone IS NULL;

-- 'phone' obligatorio
ALTER TABLE identity.persons
    ALTER COLUMN phone SET NOT NULL;

COMMIT;
