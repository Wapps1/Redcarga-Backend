package com.app.redcarga.planning.interfaces.acl;

import java.time.Instant;

/**
 * Facade pública de Planning para que otros BCs (Requests) disparen matching + notificaciones,
 * y para cerrar el buzón de una solicitud.
 */
public interface PlanningMatchingFacade {

    /**
     * Dispara el matching y, tras commit, las notificaciones WS.
     * También persiste en request_inbox de manera idempotente.
     */
    void matchAndNotify(PlanningMatchCommand cmd);

    /**
     * Cierra todas las entradas OPEN del request en el inbox
     * y envía REQUEST_CLOSED a las compañías afectadas.
     */
    void closeInboxForRequest(int requestId, boolean notify);

    /* ===== Contrato ===== */
    record PlanningMatchCommand(
            int requestId,
            String originDepartmentCode,
            String destDepartmentCode,
            String originProvinceCode,
            String destProvinceCode,
            Instant createdAt,
            String requesterName
    ) {}
}
