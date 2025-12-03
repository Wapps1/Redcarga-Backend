package com.app.redcarga.tracking.application.internal.commandservices;

import com.app.redcarga.tracking.domain.model.aggregates.CurrentQuoteLocation;
import com.app.redcarga.tracking.domain.repositories.CurrentQuoteLocationRepository;
import com.app.redcarga.tracking.domain.services.TrackingCommandService;
import lombok.RequiredArgsConstructor;
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
public class TrackingCommandServiceImpl implements TrackingCommandService {

    private final CurrentQuoteLocationRepository repo;

    @Value("${tracking.persistence.min-update-seconds:5}")
    private int minUpdateSeconds;

    @Override
    @Transactional
    public void updateLocation(int quoteId, int driverId, double lat, double lng) {
        Instant now = Instant.now();
        BigDecimal latBd = BigDecimal.valueOf(lat).setScale(6, RoundingMode.HALF_UP);
        BigDecimal lngBd = BigDecimal.valueOf(lng).setScale(6, RoundingMode.HALF_UP);

        Optional<CurrentQuoteLocation> opt = repo.findByQuoteId(quoteId);
        if (opt.isEmpty()) {
            CurrentQuoteLocation created = CurrentQuoteLocation.createFirst(quoteId, driverId, latBd, lngBd, now);
            repo.save(created);
            return;
        }

        CurrentQuoteLocation existing = opt.get();
        Instant updatedAt = existing.getUpdatedAt();
        long elapsed = Duration.between(updatedAt, now).getSeconds();
        if (elapsed >= minUpdateSeconds) {
            existing.updateLocation(latBd, lngBd, now);
            repo.save(existing);
        }
        // otherwise skip persistence
    }
}
