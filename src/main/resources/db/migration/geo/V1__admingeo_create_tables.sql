-- Schema para el catálogo geográfico
CREATE SCHEMA IF NOT EXISTS geo;

-- Departamentos (código UBIGEO de 2 dígitos)
CREATE TABLE IF NOT EXISTS geo.departments (
    department_code CHAR(2) PRIMARY KEY,         -- ej. '15'
    department_name TEXT NOT NULL
    );

-- Provincias (código UBIGEO de 4 dígitos)
CREATE TABLE IF NOT EXISTS geo.provinces (
    province_code CHAR(4) PRIMARY KEY,           -- ej. '1508'
    department_code CHAR(2) NOT NULL REFERENCES geo.departments(department_code),
    province_name TEXT NOT NULL,
    -- Garantiza coherencia del prefijo (DDPP debe empezar por DD del depto)
    CONSTRAINT provinces_prefix_chk
    CHECK (SUBSTRING(province_code FROM 1 FOR 2) = department_code)
    );

-- Para listar provincias por departamento rápido
CREATE INDEX IF NOT EXISTS ix_provinces_by_department
    ON geo.provinces (department_code, province_name);
