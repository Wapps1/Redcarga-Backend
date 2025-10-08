package com.app.redcarga.fleet.domain.model.aggregates;

import com.app.redcarga.fleet.domain.model.valueobjects.Plate;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "fleet", name = "vehicles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_vehicles_company_plate", columnNames = {"company_id", "plate"})
        })
@AttributeOverride(name = "id", column = @Column(name = "vehicle_id"))
public class Vehicle extends AuditableAbstractAggregateRoot<Vehicle> {

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "plate", nullable = false, length = 16)
    private String plate;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public static Vehicle create(Integer companyId, String name, String plate, Boolean active) {
        Vehicle v = new Vehicle();
        v.companyId = requirePositive(companyId, "companyId");
        v.name = normalizeRequired(name, 1, 120, "name");
        v.plate = Plate.of(plate).value();
        v.active = (active != null) ? active : true;
        return v;
    }

    public void update(String name, String plate, Boolean active) {
        if (name != null && !name.isBlank()) {
            this.name = normalizeRequired(name, 1, 120, "name");
        }
        if (plate != null && !plate.isBlank()) {
            this.plate = Plate.of(plate).value();
        }
        if (active != null) {
            this.active = active;
        }
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    private static Integer requirePositive(Integer v, String field) {
        if (v == null || v <= 0) throw new IllegalArgumentException(field + "_invalid");
        return v;
    }

    private static String normalizeRequired(String raw, int min, int max, String field) {
        if (raw == null) throw new IllegalArgumentException(field + "_required");
        String v = raw.trim();
        if (v.length() < min || v.length() > max) throw new IllegalArgumentException(field + "_length");
        return v;
    }
}


