package com.app.redcarga.providers.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.providers.domain.model.aggregates.CompanyMember;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyMemberId;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRepository;
import com.app.redcarga.providers.domain.model.valueobjects.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCompanyMemberRepository
        extends JpaRepository<CompanyMember, CompanyMemberId>, CompanyMemberRepository {

    // Derivada sobre el EmbeddedId + status
    boolean existsByIdAccountIdAndStatus(Integer accountId, MembershipStatus status);

    @Override
    default boolean existsActiveByAccountId(Integer accountId) {
        return existsByIdAccountIdAndStatus(accountId, MembershipStatus.ACTIVE);
    }

    boolean existsByIdCompanyIdAndIdAccountIdAndStatus(Integer companyId, Integer accountId, MembershipStatus status);
    @Override
    default boolean existsActiveByCompanyIdAndAccountId(Integer companyId, Integer accountId) {
        return existsByIdCompanyIdAndIdAccountIdAndStatus(companyId, accountId, MembershipStatus.ACTIVE);
    }

    @Query("select m.id.companyId from CompanyMember m " +
           "where m.id.accountId = :accountId and m.status = com.app.redcarga.providers.domain.model.valueobjects.MembershipStatus.ACTIVE " +
           "order by m.joinedAt asc")
    List<Integer> findActiveCompanyIdsByAccount(@Param("accountId") Integer accountId);

    @Override
    default Optional<Integer> findAnyActiveByAccount(Integer accountId) {
        List<Integer> ids = findActiveCompanyIdsByAccount(accountId);
        return ids == null || ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }
}
