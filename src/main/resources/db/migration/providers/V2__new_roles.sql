-- Semilla de catálogo de roles de compañía
INSERT INTO providers.company_roles (code, name)
VALUES ('DRIVER', 'Conductor')
    ON CONFLICT (code) DO NOTHING;
