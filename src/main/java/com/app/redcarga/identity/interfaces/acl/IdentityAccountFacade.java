package com.app.redcarga.identity.interfaces.acl;

import java.util.Optional;

public interface IdentityAccountFacade {
    Optional<IdentityPersonSnapshot> findByAccountId(Integer accountId);
}