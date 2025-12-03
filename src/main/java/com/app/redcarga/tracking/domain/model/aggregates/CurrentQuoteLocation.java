package com.app.redcarga.tracking.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(schema = "tracking", name = "quote_current_location")
public class CurrentQuoteLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "current_location_id", nullable = false)
    private Integer currentLocationId;

    @Column(name = "quote_id", nullable = false)
    private Integer quoteId;

    @Column(name = "driver_id", nullable = false)
    private Integer driverId;

    @Column(name = "lat", precision = 9, scale = 6, nullable = false)
    private BigDecimal lat;

    @Column(name = "lng", precision = 9, scale = 6, nullable = false)
    private BigDecimal lng;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static CurrentQuoteLocation createFirst(Integer quoteId, Integer driverId, BigDecimal lat, BigDecimal lng, Double speed, Instant now) {
        if (quoteId == null || driverId == null) throw new IllegalArgumentException("invalid_ids");
        var c = new CurrentQuoteLocation();
        c.quoteId = quoteId;
        c.driverId = driverId;
        c.lat = lat;
        c.lng = lng;
        c.speed = speed;
        c.updatedAt = now;
        return c;
    }

    public void updateLocation(BigDecimal lat, BigDecimal lng, Instant when) {
        this.lat = lat;
        this.lng = lng;
        this.updatedAt = when;
    }

    public void updateLocationWithSpeed(BigDecimal lat, BigDecimal lng, Double speed, Instant when) {
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
        this.updatedAt = when;
    }
}
