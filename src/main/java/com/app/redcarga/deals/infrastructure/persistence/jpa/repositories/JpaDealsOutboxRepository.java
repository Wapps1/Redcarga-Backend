package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.infrastructure.persistence.jpa.entities.DealsOutboxEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaDealsOutboxRepository extends JpaRepository<DealsOutboxEntry, Integer> {
    List<DealsOutboxEntry> findByProcessedAtIsNullOrderByCreatedAtAsc();

    List<DealsOutboxEntry> findByRouteKindAndProcessedAtIsNullOrderByCreatedAtAsc(String routeKind);
}
