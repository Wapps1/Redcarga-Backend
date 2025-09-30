package com.app.redcarga.iam.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.model.valueobjects.SignupStatus;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaSignupIntentRepository extends JpaRepository<SignupIntent, Integer>, SignupIntentRepository {

    @Override
    default Optional<SignupIntent> findOpenByAccountId(Integer accountId) {
        Set<SignupStatus> open = EnumSet.of(
                SignupStatus.PENDING_EMAIL_VERIFICATION,
                SignupStatus.EMAIL_VERIFIED,
                SignupStatus.BASIC_PROFILE_COMPLETED
        );
        return findFirstByAccountIdAndStatusInOrderByIdDesc(accountId, open);
    }

    @Query("""
           select s from SignupIntent s
           where s.accountId = :accountId and s.status in :statuses
           order by s.id desc
           """)
    Optional<SignupIntent> findFirstByAccountIdAndStatusInOrderByIdDesc(Integer accountId, Set<SignupStatus> statuses);
}
