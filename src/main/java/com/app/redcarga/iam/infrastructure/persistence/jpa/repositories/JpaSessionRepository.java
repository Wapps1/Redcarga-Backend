package com.app.redcarga.iam.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.iam.domain.model.aggregates.Session;
import com.app.redcarga.iam.domain.model.valueobjects.Platform;
import com.app.redcarga.iam.domain.model.valueobjects.SessionStatus;
import com.app.redcarga.iam.domain.repositories.SessionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSessionRepository extends JpaRepository<Session, Integer>, SessionRepository {

    List<Session> findByAccountIdAndStatus(Integer accountId, SessionStatus status);

    Optional<Session> findFirstByAccountIdAndPlatformAndStatus(Integer accountId, Platform platform, SessionStatus status);

    @Override
    default Optional<Session> findActiveByAccountIdAndPlatform(Integer accountId, Platform platform) {
        return findFirstByAccountIdAndPlatformAndStatus(accountId, platform, SessionStatus.ACTIVE);
    }
}
