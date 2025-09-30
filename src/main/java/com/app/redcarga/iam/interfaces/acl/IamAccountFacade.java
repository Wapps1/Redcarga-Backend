package com.app.redcarga.iam.interfaces.acl;

import java.util.Optional;

public interface IamAccountFacade {
    Optional<IamAccountSnapshot> findByAccountId(int accountId);
    Optional<IamAccountSnapshot> findByExternalUid(String uid); // NUEVO
    Optional<IamAccountSnapshot> findByEmail(String email);
}
