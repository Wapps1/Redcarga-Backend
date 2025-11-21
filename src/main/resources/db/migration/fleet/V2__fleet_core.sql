-- V25__drivers_refactor.sql

-- 1) Eliminar todos los registros previos
DELETE FROM fleet.drivers;

-- 2) Eliminar columnas antiguas
ALTER TABLE fleet.drivers
    DROP COLUMN first_name,
    DROP COLUMN last_name,
    DROP COLUMN email,
    DROP COLUMN phone;

-- 3) Agregar columna nueva
ALTER TABLE fleet.drivers
    ADD COLUMN account_id INTEGER NOT NULL;
