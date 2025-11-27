package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAssignmentRepository extends JpaRepository<Assignment, Integer>, AssignmentRepository {
    Optional<Assignment> findByQuoteId(Integer quoteId);

    @Query("select a from Assignment a, com.app.redcarga.deals.domain.model.aggregates.Quote q " +
            "where q.id = a.quoteId and a.driverId = :driverId and q.stateCode = :state")
    List<Assignment> findByDriverIdAndQuoteState(@Param("driverId") Integer driverId, @Param("state") String state);
}
