package com.app.redcarga.requests.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaRequestRepository
        extends JpaRepository<Request, Integer>, RequestRepository {
    @Override
    List<Request> findAllByRequesterAccountId(Integer requesterAccountId);
}
