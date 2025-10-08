package com.app.redcarga.requests.application.internal.outboundservices.acl;

import com.app.redcarga.identity.interfaces.acl.IdentityAccountFacade;
import com.app.redcarga.requests.application.internal.outboundservices.acl.IdentityPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("requestsIdentityPersonServiceClient")
@RequiredArgsConstructor
public class IdentityPersonServiceClient implements IdentityPersonService {
    private final IdentityAccountFacade identity;

    @Override
    public Optional<PersonSnapshot> getByAccountId(int accountId) {
        return identity.findByAccountId(accountId)
                .map(s -> new PersonSnapshot(s.accountId(), s.fullName(), s.docNumber()));
    }
}
