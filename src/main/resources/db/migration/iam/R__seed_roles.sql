INSERT INTO iam.system_roles(code, display_name, description) VALUES
    ('CLIENT','Cliente','Usuario cliente'),
    ('PROVIDER','Proveedor','Usuario proveedor'),
    ('PLATFORM_ADMIN','Admin Plataforma','Administrador')
ON CONFLICT (code) DO NOTHING;
