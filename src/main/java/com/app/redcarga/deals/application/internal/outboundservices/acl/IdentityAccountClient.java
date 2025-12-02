package com.app.redcarga.deals.application.internal.outboundservices.acl;

import com.app.redcarga.identity.interfaces.acl.IdentityAccountFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Pass-through client to Identity ACL.
 * Returns the raw snapshot so caller decides how to extract fullname.
 */
@Service
@RequiredArgsConstructor
public class IdentityAccountClient {

    private final IdentityAccountFacade identityAccountFacade;

    public Optional<Object> findByAccountId(Integer accountId) {
        return identityAccountFacade.findByAccountId(accountId).map(s -> (Object) s);
    }
}