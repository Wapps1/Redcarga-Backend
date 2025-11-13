package com.app.redcarga.requests.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRequestRepository
        extends JpaRepository<Request, Integer>, RequestRepository {
    @Override
    List<Request> findAllByRequesterAccountId(Integer requesterAccountId);

    @Override
    @Query("""
        select distinct r
        from Request r
        left join fetch r.items it
        where r.id = :id
    """)
    Optional<Request> findByIdWithItemsAndImages(@Param("id") Integer id);
}
