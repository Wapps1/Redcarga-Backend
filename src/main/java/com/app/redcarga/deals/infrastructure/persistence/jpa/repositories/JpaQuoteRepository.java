package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaQuoteRepository extends JpaRepository<Quote, Integer>, QuoteRepository {

    @Override
    Optional<Quote> findById(Integer id);

    List<Quote> findByRequestIdAndStateCode(Integer requestId, String stateCode);

    List<Quote> findByRequestId(Integer requestId);

    // Company filters
    List<Quote> findByCompanyId(Integer companyId);
    List<Quote> findByCompanyIdAndStateCode(Integer companyId, String stateCode);
    List<Quote> findByCompanyIdAndStateCodeIn(Integer companyId, java.util.Collection<String> stateCodes);

    @Transactional
    @Modifying
    @Query("UPDATE Quote q SET q.stateCode = :toState WHERE q.requestId = :requestId AND q.id <> :excludedQuoteId AND q.stateCode = :fromState")
    int updateStateForRequestExcept(@Param("requestId") Integer requestId,
                                    @Param("excludedQuoteId") Integer excludedQuoteId,
                                    @Param("fromState") String fromState,
                                    @Param("toState") String toState);

    @Query("SELECT q.version FROM Quote q WHERE q.id = :quoteId")
    Optional<Integer> findVersionByQuoteId(@Param("quoteId") Integer quoteId);

}