-- Estados de cotización
INSERT INTO deals.catalog_quote_state(code,name) VALUES
                                                     ('PENDIENTE','Pendiente'),
                                                     ('TRATO','Trato'),
                                                     ('EN_ESPERA','En espera'),
                                                     ('ACEPTADA','Aceptada'),
                                                     ('RECHAZADA','Rechazada'),
                                                     ('CERRADA','Cerrada'),
                                                     ('CERRADA_NO_ADJ','Cerrada no adjudicada')
    ON CONFLICT (code) DO NOTHING;

-- Cambios: kind y status
INSERT INTO deals.catalog_change_kind(code) VALUES ('LIBRE'),('PROPUESTA')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO deals.catalog_change_status(code) VALUES ('APLICADO'),('PENDIENTE'),('RECHAZADO')
    ON CONFLICT (code) DO NOTHING;

-- Chat: tipos y subtipos
INSERT INTO deals.catalog_chat_type(code) VALUES ('USER'),('SYSTEM')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO deals.catalog_chat_subtype(code) VALUES
                                                 ('CHANGE_APPLIED'),('CHANGE_PROPOSED'),('CHANGE_ACCEPTED'),('CHANGE_REJECTED'),
                                                 ('STATE_TRANSITION'),('INFO'),
                                                 ('ACCEPTANCE_REQUEST'),('ACCEPTANCE_CONFIRMED'),('ACCEPTANCE_REJECTED')
    ON CONFLICT (code) DO NOTHING;

-- Checklist catálogos
INSERT INTO deals.catalog_actor_role(code) VALUES ('CLIENTE'),('PROVEEDOR')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO deals.catalog_checklist_kind(code) VALUES ('DOC'),('ACTION'),('INFO')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO deals.catalog_checklist_status(code) VALUES ('PENDING'),('DONE'),('WAIVED')
    ON CONFLICT (code) DO NOTHING;

-- Pagos
INSERT INTO deals.catalog_payment_status(code) VALUES ('MADE'),('CONFIRMED')
    ON CONFLICT (code) DO NOTHING;

-- =========================================================
-- Plantilla por defecto (idempotente)
-- =========================================================

-- 1) Asegura la plantilla por nombre
INSERT INTO deals.checklist_template(name, is_default, active)
SELECT 'Default envío de carga', TRUE, TRUE
    WHERE NOT EXISTS (
  SELECT 1 FROM deals.checklist_template WHERE name = 'Default envío de carga'
);

-- 2) Usa ese template_id por nombre (estable y sin CTE)
INSERT INTO deals.checklist_template_item(template_id, code, name, actor_code, kind_code, required)
SELECT t.template_id, v.code, v.name, v.actor, v.kind, v.required
FROM deals.checklist_template t
         JOIN (VALUES
                   ('DOC_GRE_REMITENTE','GRE remitente','CLIENTE','DOC', TRUE),
                   ('DOC_GRE_TRANSPORTISTA','GRE transportista','PROVEEDOR','DOC', TRUE),
                   ('PAYMENT_MADE','Pago realizado','CLIENTE','ACTION', TRUE),
                   ('PAYMENT_CONFIRMED','Pago confirmado','PROVEEDOR','ACTION', TRUE),
                   ('ASSIGNMENT_SET','Asignación de conductor/vehículo','PROVEEDOR','ACTION', TRUE),
                   ('SHIPMENT_SENT','Carga enviada','PROVEEDOR','ACTION', TRUE),
                   ('SHIPMENT_RECEIVED','Carga recibida','CLIENTE','ACTION', TRUE)
) AS v(code,name,actor,kind,required)
              ON t.name = 'Default envío de carga'
    ON CONFLICT (template_id, code) DO NOTHING;

-- 3) Dependencias (idempotentes) para ese mismo template
INSERT INTO deals.checklist_template_dependency(template_id, item_code, depends_on_code)
SELECT t.template_id, x.item_code, x.depends_on
FROM deals.checklist_template t
         JOIN (VALUES
                   ('PAYMENT_CONFIRMED','PAYMENT_MADE'),
                   ('SHIPMENT_SENT','DOC_GRE_REMITENTE'),
                   ('SHIPMENT_SENT','DOC_GRE_TRANSPORTISTA'),
                   ('SHIPMENT_SENT','PAYMENT_CONFIRMED'),
                   ('SHIPMENT_SENT','ASSIGNMENT_SET'),
                   ('SHIPMENT_RECEIVED','SHIPMENT_SENT')
) AS x(item_code, depends_on)
              ON t.name = 'Default envío de carga'
    ON CONFLICT (template_id, item_code, depends_on_code) DO NOTHING;
