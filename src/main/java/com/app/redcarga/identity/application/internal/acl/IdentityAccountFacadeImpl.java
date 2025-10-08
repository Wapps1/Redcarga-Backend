// ...existing code...
package com.app.redcarga.identity.application.internal.acl;

import com.app.redcarga.identity.domain.model.aggregates.Person;
import com.app.redcarga.identity.domain.services.PersonQueryService;
import com.app.redcarga.identity.interfaces.acl.IdentityAccountFacade;
import com.app.redcarga.identity.interfaces.acl.IdentityPersonSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdentityAccountFacadeImpl implements IdentityAccountFacade {

    private final PersonQueryService personQueryService;

    @Override
    public Optional<IdentityPersonSnapshot> findByAccountId(Integer accountId) {
        return personQueryService.findByAccountId(accountId)
                .map(p -> new IdentityPersonSnapshot(
                        accountId,
                        p.getFullName(),     // ya es String
                        p.getDocNumber()     // ya es String
                ));
    }
}