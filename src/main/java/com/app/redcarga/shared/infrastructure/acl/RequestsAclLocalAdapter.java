package com.app.redcarga.shared.infrastructure.acl;

import com.app.redcarga.shared.ws.auth.RequestOwnershipVerifierPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Fallback adapter for RequestOwnershipVerifierPort only if no implementation is present.
 * Does not depend on RequestFacade to avoid cross-BC coupling and bean cycles.
 */
@Component
@ConditionalOnMissingBean(RequestOwnershipVerifierPort.class)
public class RequestsAclLocalAdapter implements RequestOwnershipVerifierPort {

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        // No-op fallback; real implementation should be provided by Requests BC.
        throw new IllegalStateException("No RequestOwnershipVerifierPort implementation available");
    }
}
