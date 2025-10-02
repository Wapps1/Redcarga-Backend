-- Semilla de catálogo de roles de compañía
INSERT INTO providers.company_roles (code, name)
VALUES ('ADMIN', 'Administrador')
    ON CONFLICT (code) DO NOTHING;
