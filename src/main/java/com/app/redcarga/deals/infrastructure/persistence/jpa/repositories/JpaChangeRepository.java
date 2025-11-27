package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.repositories.ChangeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface JpaChangeRepository extends JpaRepository<Change, Integer>, ChangeRepository {

    @Override
    Optional<Change> findById(Integer id);

    // Para command service: trae change + items + quote con JOIN FETCH
    @Override
    @Query("SELECT c FROM Change c LEFT JOIN FETCH c.items LEFT JOIN FETCH c.quote WHERE c.changeId = :id")
    Optional<Change> findByIdWithItems(@Param("id") Integer id);

    // Listar pendientes por quote
    @Override
    @Query("SELECT c FROM Change c WHERE c.quote.id = :quoteId AND c.statusCode = 'PENDIENTE'")
    List<Change> findPendingByQuoteId(@Param("quoteId") Integer quoteId);
}