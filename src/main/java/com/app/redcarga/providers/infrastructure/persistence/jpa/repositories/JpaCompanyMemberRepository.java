package com.app.redcarga.providers.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.providers.domain.model.aggregates.CompanyMember;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyMemberId;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRepository;
import com.app.redcarga.providers.domain.model.valueobjects.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCompanyMemberRepository
        extends JpaRepository<CompanyMember, CompanyMemberId>, CompanyMemberRepository {

    // Derivada sobre el EmbeddedId + status
    boolean existsByIdAccountIdAndStatus(Integer accountId, MembershipStatus status);

    @Override
    default boolean existsActiveByAccountId(Integer accountId) {
        return existsByIdAccountIdAndStatus(accountId, MembershipStatus.ACTIVE);
    }
}
