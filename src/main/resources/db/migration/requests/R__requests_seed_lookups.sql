-- =====================================================
-- Seeds de catálogos del BC Requests
-- =====================================================

-- Estados de la solicitud
INSERT INTO requests.request_statuses (status_id, code) VALUES
    (1, 'OPEN'),
    (2, 'CLOSED'),
    (3, 'CANCELLED'),
    (4, 'EXPIRED')
    ON CONFLICT (status_id) DO NOTHING;

-- Motivos de cierre/cambio
INSERT INTO requests.request_close_reasons (reason_id, code) VALUES
    (1, 'USER_CLOSED'),
    (2, 'MATCHED'),
    (3, 'TIMEOUT'),
    (4, 'CANCELLED')
    ON CONFLICT (reason_id) DO NOTHING;

-- Estados del outbox
INSERT INTO requests.outbox_statuses (outbox_status_id, code) VALUES
    (1, 'PENDING'),
    (2, 'SENT'),
    (3, 'DEAD')
    ON CONFLICT (outbox_status_id) DO NOTHING;
