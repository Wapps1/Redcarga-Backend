package com.app.redcarga.fleet.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.fleet.domain.model.aggregates.Vehicle;
import com.app.redcarga.fleet.domain.repositories.VehicleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaVehicleRepository extends JpaRepository<Vehicle, Integer>, VehicleRepository {

    @Override
    Optional<Vehicle> findById(Integer id);

    @Override
    boolean existsByCompanyIdAndPlate(Integer companyId, String plate);

    @Override
    List<Vehicle> findAllByCompanyId(Integer companyId);
}


