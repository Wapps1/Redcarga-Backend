package com.app.redcarga.shared.ws.auth;

/**
 * Port used by shared WS infra to verify that a given accountId is the owner
 * (requester) of a requestId. Implemented by the Requests BC.
 */
public interface RequestOwnershipVerifierPort {
    boolean isRequester(Integer requestId, Integer accountId);
}
