package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaChangeRepository extends JpaRepository<Change, Integer> {
}
