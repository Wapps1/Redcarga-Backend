package com.app.redcarga.fleet.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.fleet.domain.model.aggregates.Driver;
import com.app.redcarga.fleet.domain.repositories.DriverRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaDriverRepository extends JpaRepository<Driver, Integer>, DriverRepository {

    @Override
    Optional<Driver> findById(Integer id);

    @Override
    boolean existsByCompanyIdAndLicenseNumber(Integer companyId, String licenseNumber);

    @Override
    List<Driver> findAllByCompanyId(Integer companyId);
}


