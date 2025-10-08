package com.app.redcarga.fleet.domain.repositories;

import com.app.redcarga.fleet.domain.model.aggregates.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Integer id);
    boolean existsByCompanyIdAndPlate(Integer companyId, String plate);
    List<Vehicle> findAllByCompanyId(Integer companyId);
    void delete(Vehicle vehicle);
}


