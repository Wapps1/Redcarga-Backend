package com.app.redcarga.planning.interfaces.acl;

/**
 * ACL público de Planning.
 * Expone operaciones para gestionar request_inbox desde otros BCs.
 */
public interface PlanningFacade {

    /**
     * Cambia el status de un request_inbox específico.
     * @param companyId ID de la empresa
     * @param requestId ID de la solicitud
     * @param newStatus nuevo status (e.g., 'OPEN', 'CLOSED', 'QUOTE')
     */
    void updateRequestInboxStatus(int companyId, int requestId, String newStatus);

    /**
     * Cierra todos los request_inbox de un requestId (marca como 'CLOSED').
     * @param requestId ID de la solicitud
     */
    void closeAllRequestInboxForRequest(int requestId);
}
