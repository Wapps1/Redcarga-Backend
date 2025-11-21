package com.app.redcarga.fleet.application.internal.outboundservices.acl;

import com.app.redcarga.identity.interfaces.acl.IdentityAccountFacade;
import com.app.redcarga.identity.interfaces.acl.IdentityPersonSnapshot;
import com.app.redcarga.requests.application.internal.outboundservices.acl.IdentityPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdentityAccountFleetServiceClient implements IdentityAccountFleetService {

    private final IdentityAccountFacade identity;

    public Optional<IdentityPersonSnapshot> findByAccountId(Integer accountId){
        return identity.findByAccountId(accountId);
    }
}
