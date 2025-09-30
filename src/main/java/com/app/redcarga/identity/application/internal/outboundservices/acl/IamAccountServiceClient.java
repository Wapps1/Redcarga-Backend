package com.app.redcarga.identity.application.internal.outboundservices.acl;

import com.app.redcarga.iam.interfaces.acl.IamAccountFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IamAccountServiceClient implements IamAccountService {

    private final IamAccountFacade iamFacade;

    @Override
    public Optional<AccountSnapshot> getByAccountId(int accountId) {
        return iamFacade.findByAccountId(accountId)
                .map(s -> new AccountSnapshot(
                        s.accountId(),
                        s.signupIntentState(),
                        s.systemRoleCode(),
                        s.emailVerified()
                ));
    }

    @Override
    public Optional<AccountSnapshot> getByExternalUid(String uid) {
        return iamFacade.findByExternalUid(uid)
                .map(s -> new AccountSnapshot(s.accountId(), s.signupIntentState(), s.systemRoleCode(), s.emailVerified()));
    }

    @Override
    public Optional<AccountSnapshot> getByEmail(String email) {
        return iamFacade.findByEmail(email)
                .map(s -> new AccountSnapshot(s.accountId(), s.signupIntentState(), s.systemRoleCode(), s.emailVerified()));
    }
}
