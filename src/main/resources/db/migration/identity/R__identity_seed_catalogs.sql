INSERT INTO identity.doc_types(code, name) VALUES
                                               ('DNI','Documento Nacional de Identidad'),
                                               ('CE','Carné de Extranjería'),
                                               ('PAS','Pasaporte')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO identity.kyc_status(code, name) VALUES
                                                ('CREATED','Creada'),
                                                ('SUBMITTED','Enviada'),
                                                ('PASSED','Aprobada'),
                                                ('FAILED','Rechazada'),
                                                ('EXPIRED','Expirada')
    ON CONFLICT (code) DO NOTHING;
