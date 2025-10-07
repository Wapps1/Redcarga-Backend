package com.app.redcarga.fleet.domain.services;

import com.app.redcarga.fleet.domain.model.aggregates.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleQueryService {
    Optional<Vehicle> findById(Integer vehicleId);
    List<Vehicle> findAllByCompany(Integer companyId);
}


