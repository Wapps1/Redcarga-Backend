INSERT INTO planning.route_types (route_type_id, code, name) VALUES
 (1, 'DD', 'Departamento a Departamento'),
 (2, 'PP', 'Provincia a Provincia')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;