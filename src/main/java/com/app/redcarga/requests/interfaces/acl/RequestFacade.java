package com.app.redcarga.requests.interfaces.acl;

/**
 * Facade público del BC requests para comprobar ownership simple.
 * Devuelve true si accountId es el requester_account_id del request.
 */
public interface RequestFacade {
    boolean isRequester(Integer requestId, Integer accountId);

    /** Snapshot used by other BCs to decide flows (accepted quote + request status id) */
    record RequestAcceptanceSnapshot(Integer requestId, Integer acceptedQuoteId, Integer statusId) {}

    java.util.Optional<RequestAcceptanceSnapshot> getAcceptanceSnapshot(Integer requestId);

}