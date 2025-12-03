package com.app.redcarga.tracking.application.internal.queryservices;

import com.app.redcarga.tracking.application.internal.outboundservices.acl.DealsFacadeClient;
import com.app.redcarga.tracking.domain.repositories.CurrentQuoteLocationRepository;
import com.app.redcarga.tracking.domain.services.TrackingQueryService;
import com.app.redcarga.tracking.interfaces.rest.responses.CurrentLocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackingQueryServiceImpl implements TrackingQueryService {

    private final CurrentQuoteLocationRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentLocationResponse> getCurrentLocationByQuoteId(int quoteId) {
        return repo.findByQuoteId(quoteId)
                .map(e -> new CurrentLocationResponse(
                        e.getQuoteId(),
                        e.getDriverId(),
                        e.getLat().doubleValue(),
                        e.getLng().doubleValue(),
                        e.getSpeed(),
                        e.getUpdatedAt().toString()
                ));
    }
}
