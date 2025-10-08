package com.app.redcarga.fleet.domain.services;

import com.app.redcarga.fleet.domain.model.aggregates.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverQueryService {
    Optional<Driver> findById(Integer driverId);
    List<Driver> findAllByCompany(Integer companyId);
}


