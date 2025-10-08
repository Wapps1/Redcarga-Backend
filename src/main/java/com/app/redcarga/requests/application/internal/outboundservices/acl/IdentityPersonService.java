// application.internal.outboundservices.acl.IdentityPersonService
package com.app.redcarga.requests.application.internal.outboundservices.acl;

import java.util.Optional;

public interface IdentityPersonService {
    Optional<PersonSnapshot> getByAccountId(int accountId);

    record PersonSnapshot(Integer accountId, String fullName, String docNumber) {}
}
