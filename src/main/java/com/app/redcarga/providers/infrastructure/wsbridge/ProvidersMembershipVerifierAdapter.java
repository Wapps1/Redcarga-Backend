package com.app.redcarga.providers.infrastructure.wsbridge;

import com.app.redcarga.shared.ws.auth.MembershipVerifierPort;
import com.app.redcarga.providers.interfaces.acl.ProvidersMembershipFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Puente WS -> Providers ACL.
 * Implementa el puerto compartido y delega al facade público de Providers.
 */
@Component
@RequiredArgsConstructor
public class ProvidersMembershipVerifierAdapter implements MembershipVerifierPort {

    private final ProvidersMembershipFacade facade;

    @Override
    public boolean isActiveMember(int companyId, int accountId) {
        // Garantiza "ACTIVO" según la semántica del facade.
        return facade.isMemberOfCompany(companyId, accountId);
    }
}
