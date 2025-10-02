package com.app.redcarga.providers.application.internal.outboundservices.acl;

import com.app.redcarga.iam.interfaces.acl.IamAccountFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IamAccountProviderServiceClient implements IamAccountProviderService {

    private final IamAccountFacade iamFacade;

    @Override
    public Optional<AccountProviderSnapshot> getByAccountId(int accountId) {
        return iamFacade.findByAccountId(accountId)
                .map(s -> new AccountProviderSnapshot(
                        s.accountId(),
                        s.signupIntentState(),
                        s.systemRoleCode(),
                        s.emailVerified()
                ));
    }

    @Override
    public Optional<AccountProviderSnapshot> getByExternalUid(String uid) {
        return iamFacade.findByExternalUid(uid)
                .map(s -> new AccountProviderSnapshot(
                        s.accountId(),
                        s.signupIntentState(),
                        s.systemRoleCode(),
                        s.emailVerified()
                ));
    }

    @Override
    public Optional<AccountProviderSnapshot> getByEmail(String email) {
        return iamFacade.findByEmail(email)
                .map(s -> new AccountProviderSnapshot(
                        s.accountId(),
                        s.signupIntentState(),
                        s.systemRoleCode(),
                        s.emailVerified()
                ));
    }
}
