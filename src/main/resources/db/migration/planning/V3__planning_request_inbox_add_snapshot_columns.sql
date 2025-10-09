-- =====================================================
-- BC: Planning — request_inbox — add non-null snapshot columns
-- Context: existing rows -> dummy data, then enforce NOT NULL
-- =====================================================

-- 1. Agregar columnas nuevas (inicialmente NULL)
ALTER TABLE planning.request_inbox
    ADD COLUMN requester_name           TEXT,
  ADD COLUMN origin_department_name   TEXT,
  ADD COLUMN origin_province_name     TEXT,
  ADD COLUMN dest_department_name     TEXT,
  ADD COLUMN dest_province_name       TEXT,
  ADD COLUMN total_quantity           INTEGER;

-- 2. Poblar data existente con valores por defecto (dummy)
UPDATE planning.request_inbox
SET requester_name = COALESCE(requester_name, 'unknown'),
    origin_department_name = COALESCE(origin_department_name, 'unknown'),
    origin_province_name   = COALESCE(origin_province_name, 'unknown'),
    dest_department_name   = COALESCE(dest_department_name, 'unknown'),
    dest_province_name     = COALESCE(dest_province_name, 'unknown'),
    total_quantity         = COALESCE(total_quantity, 0);

-- 3. Convertir las columnas a NOT NULL
ALTER TABLE planning.request_inbox
    ALTER COLUMN requester_name           SET NOT NULL,
ALTER COLUMN origin_department_name   SET NOT NULL,
  ALTER COLUMN origin_province_name     SET NOT NULL,
  ALTER COLUMN dest_department_name     SET NOT NULL,
  ALTER COLUMN dest_province_name       SET NOT NULL,
  ALTER COLUMN total_quantity           SET NOT NULL;
