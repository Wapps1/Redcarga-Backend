package com.app.redcarga.fleet.application.internal.outboundservices.acl;

import com.app.redcarga.identity.interfaces.acl.IdentityPersonSnapshot;

import java.util.Optional;

public interface IdentityAccountFleetService {

    Optional<IdentityPersonSnapshot> findByAccountId(Integer accountId);

    record PersonSnapshot(Integer accountId, String fullName, String docNumber, String phone) {}
}
