package com.app.redcarga.providers.interfaces.acl;

import java.util.Optional;

public interface ProvidersMembershipFacade {
    /**
     * @return true si el accountId es miembro ACTIVO de companyId.
     */
    boolean isMemberOfCompany(int companyId, int accountId);

    boolean hasAnyRole(int companyId, int accountId, int... roleCodes);

    /**
     * Retorna alguna companyId activa para el account (si existe).
     */
    Optional<Integer> findAnyActiveCompanyIdByAccount(int accountId);
}
