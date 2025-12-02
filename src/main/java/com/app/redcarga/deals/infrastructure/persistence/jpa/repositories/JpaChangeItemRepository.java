package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import com.app.redcarga.deals.domain.repositories.ChangeItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaChangeItemRepository extends JpaRepository<ChangeItem, Integer>, ChangeItemRepository {

    @Override
    @Query("SELECT ci FROM ChangeItem ci WHERE ci.change.changeId = :changeId")
    List<ChangeItem> findByChangeId(@Param("changeId") Integer changeId);
}