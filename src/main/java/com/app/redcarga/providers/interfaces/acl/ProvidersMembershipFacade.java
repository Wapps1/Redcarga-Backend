package com.app.redcarga.providers.interfaces.acl;

import java.util.Optional;
import java.util.List;

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

    /**
     * Retorna los códigos de rol activos (ej: "ADMIN", "OPERATOR") que tiene accountId en companyId.
     */
    List<String> getActiveRoleCodes(int companyId, int accountId);
}
