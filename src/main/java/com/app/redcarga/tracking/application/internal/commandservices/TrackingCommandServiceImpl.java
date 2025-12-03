package com.app.redcarga.tracking.application.internal.commandservices;

import com.app.redcarga.tracking.domain.model.aggregates.CurrentQuoteLocation;
import com.app.redcarga.tracking.domain.repositories.CurrentQuoteLocationRepository;
import com.app.redcarga.tracking.domain.services.TrackingCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingCommandServiceImpl implements TrackingCommandService {

    private final CurrentQuoteLocationRepository repo;
    private final com.app.redcarga.tracking.application.internal.outboundservices.acl.DealsFacadeClient dealsFacadeClient;

    @Value("${tracking.persistence.min-update-seconds:60}")
    private int minUpdateSeconds;

    @Override
    @Transactional
    public void updateLocation(int quoteId, int driverId, double lat, double lng, Double speed) {
        Instant now = Instant.now();
        BigDecimal latBd = BigDecimal.valueOf(lat).setScale(6, RoundingMode.HALF_UP);
        BigDecimal lngBd = BigDecimal.valueOf(lng).setScale(6, RoundingMode.HALF_UP);

        Optional<CurrentQuoteLocation> opt = repo.findByQuoteId(quoteId);
        if (opt.isEmpty()) {
            CurrentQuoteLocation created = CurrentQuoteLocation.createFirst(quoteId, driverId, latBd, lngBd, speed, now);
            repo.save(created);
            log.info("Tracking: Created FIRST location snapshot for quote={} (threshold={}s)", quoteId, minUpdateSeconds);
            return;
        }

        CurrentQuoteLocation existing = opt.get();
        Instant updatedAt = existing.getUpdatedAt();
        long elapsed = Duration.between(updatedAt, now).getSeconds();
        
        if (elapsed >= minUpdateSeconds) {
            existing.updateLocationWithSpeed(latBd, lngBd, speed, now);
            repo.save(existing);
            log.info("Tracking: PERSISTED location for quote={} (elapsed={}s >= threshold={}s)", 
                quoteId, elapsed, minUpdateSeconds);
        } else {
            log.debug("Tracking: SKIPPED persistence for quote={} (elapsed={}s < threshold={}s)", 
                quoteId, elapsed, minUpdateSeconds);
        }
        // otherwise skip persistence
    }

    @Transactional
    public void updateLocationFromAccount(int quoteId, int accountId, double lat, double lng, Double speed) {
        Integer driverId = dealsFacadeClient.findDriverIdByQuoteId(quoteId)
            .orElseThrow(() -> new IllegalStateException("no_driver_assigned"));
        updateLocation(quoteId, driverId, lat, lng, speed);
    }
}
