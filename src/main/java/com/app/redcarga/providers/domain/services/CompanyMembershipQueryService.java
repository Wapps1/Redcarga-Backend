package com.app.redcarga.providers.domain.services;
import com.app.redcarga.providers.domain.model.queries.GetMembershipQuery;
import com.app.redcarga.providers.domain.model.queries.IsActiveMemberQuery;
import com.app.redcarga.providers.interfaces.acl.ProvidersMembershipFacade;

import java.util.List;
import java.util.Optional;

public interface CompanyMembershipQueryService {
    boolean isActiveMember(IsActiveMemberQuery q);

    /** true si el miembro ACTIVO tiene al menos uno de los roles pedidos */
    boolean hasAnyRole(int companyId, int accountId, List<String> roleCodes);

    /** (opcional) lista de roles del miembro ACTIVO, útil para debugging o UI */
    List<String> getActiveRoleCodes(int companyId, int accountId);
}
