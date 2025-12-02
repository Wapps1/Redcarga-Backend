package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;
import com.app.redcarga.deals.domain.repositories.GuideRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaGuideRepository extends JpaRepository<Guide, Integer>, GuideRepository {

    List<Guide> findByQuoteId(Integer quoteId);
    Optional<Guide> findByQuoteIdAndType(Integer quoteId, GuideType type);
    boolean existsByQuoteIdAndType(Integer quoteId, GuideType type);

}