package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAssignmentRepository extends JpaRepository<Assignment, Integer>, AssignmentRepository {
    Optional<Assignment> findByQuoteId(Integer quoteId);
}
