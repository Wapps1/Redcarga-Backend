// com.app.redcarga.providers.infrastructure.persistence.jpa.repositories.JpaCompanyMemberRoleRepository
package com.app.redcarga.providers.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.providers.domain.model.entities.CompanyMemberRole;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyMemberRoleId;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface JpaCompanyMemberRoleRepository
        extends JpaRepository<CompanyMemberRole, CompanyMemberRoleId>, CompanyMemberRoleRepository {

    @Override
    @Query("""
           select r.code
           from CompanyMemberRole cmr
           join cmr.role r
           join cmr.member m
           where m.id.companyId = :companyId
             and m.id.accountId = :accountId
             and m.status = com.app.redcarga.providers.domain.model.valueobjects.MembershipStatus.ACTIVE
           """)
    List<String> findActiveRoleCodes(@Param("companyId") int companyId,
                                     @Param("accountId") int accountId);

    @Override
    @Query("""
           select (count(cmr) > 0)
           from CompanyMemberRole cmr
           join cmr.role r
           join cmr.member m
           where m.id.companyId = :companyId
             and m.id.accountId = :accountId
             and m.status = com.app.redcarga.providers.domain.model.valueobjects.MembershipStatus.ACTIVE
             and r.code in :roleCodes
           """)
    boolean hasAnyActiveRole(@Param("companyId") int companyId,
                             @Param("accountId") int accountId,
                             @Param("roleCodes") List<String> roleCodes);
}
