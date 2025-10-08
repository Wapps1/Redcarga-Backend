package com.app.redcarga.fleet.application.internal.queryservices;

import com.app.redcarga.fleet.domain.model.aggregates.Vehicle;
import com.app.redcarga.fleet.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VehicleQueryServiceImpl implements com.app.redcarga.fleet.domain.services.VehicleQueryService {

    private final VehicleRepository vehicles;

    @Override
    public Optional<Vehicle> findById(Integer vehicleId) {
        return vehicles.findById(vehicleId);
    }

    @Override
    public List<Vehicle> findAllByCompany(Integer companyId) {
        return vehicles.findAllByCompanyId(companyId);
    }
}


