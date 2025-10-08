-- =====================================================
-- BC: Requests — Add non-null request_name with backfill
-- =====================================================

-- 1) Crear columna (idempotente)
ALTER TABLE requests.requests
    ADD COLUMN IF NOT EXISTS request_name VARCHAR(200);

-- 2) Backfill para existentes (elige tu estrategia)
--    A) Nombre sintético usando el id (estable, determinista)
UPDATE requests.requests
SET request_name = 'Solicitud ' || request_id
WHERE request_name IS NULL OR request_name = '';

--    Si prefieres un placeholder fijo, usa:
-- UPDATE requests.requests
-- SET request_name = 'Sin nombre'
-- WHERE request_name IS NULL OR request_name = '';

-- 3) Reglas para nuevas filas
ALTER TABLE requests.requests
    ALTER COLUMN request_name SET DEFAULT 'Sin nombre';

-- 4) Hacerla obligatoria
ALTER TABLE requests.requests
    ALTER COLUMN request_name SET NOT NULL;
