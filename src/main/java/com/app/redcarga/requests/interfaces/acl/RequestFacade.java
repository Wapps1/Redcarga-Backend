package com.app.redcarga.requests.interfaces.acl;

import java.util.Optional;

/**
 * Facade público del BC requests para comprobar ownership simple.
 * Devuelve true si accountId es el requester_account_id del request.
 */
public interface RequestFacade {
    boolean isRequester(Integer requestId, Integer accountId);

    /** Snapshot used by other BCs to decide flows (accepted quote + request status id) */
    record RequestAcceptanceSnapshot(Integer requestId, Integer acceptedQuoteId, Integer statusId) {}

    Optional<RequestAcceptanceSnapshot> getAcceptanceSnapshot(Integer requestId);

    /**
     * Cierra una request (transición a CLOSED).
     * Llamado por otros BCs (e.g., Planning cuando se acepta una quote).
     */
    void closeRequest(Integer requestId);

}