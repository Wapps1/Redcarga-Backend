package com.app.redcarga.providers.interfaces.acl;

public interface ProvidersMembershipFacade {
    /**
     * @return true si el accountId es miembro ACTIVO de companyId.
     */
    boolean isMemberOfCompany(int companyId, int accountId);

    boolean hasAnyRole(int companyId, int accountId, String... roleCodes);

    /**
     * Retorna alguna companyId activa para el account (si existe).
     */
    java.util.Optional<Integer> findAnyActiveCompanyIdByAccount(int accountId);
}
