package com.app.redcarga.requests.interfaces.acl;

/**
 * Facade público del BC requests para comprobar ownership simple.
 * Devuelve true si accountId es el requester_account_id del request.
 */
public interface RequestFacade {
    boolean isRequester(Integer requestId, Integer accountId);
}