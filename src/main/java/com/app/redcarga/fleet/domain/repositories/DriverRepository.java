package com.app.redcarga.fleet.domain.repositories;

import com.app.redcarga.fleet.domain.model.aggregates.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Driver save(Driver driver);
    Optional<Driver> findById(Integer id);
    boolean existsByCompanyIdAndLicenseNumber(Integer companyId, String licenseNumber);
    List<Driver> findAllByCompanyId(Integer companyId);
    void delete(Driver driver);
}


