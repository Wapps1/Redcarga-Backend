package com.app.redcarga.fleet.application.internal.queryservices;

import com.app.redcarga.fleet.domain.model.aggregates.Driver;
import com.app.redcarga.fleet.domain.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DriverQueryServiceImpl implements com.app.redcarga.fleet.domain.services.DriverQueryService {

    private final DriverRepository drivers;

    @Override
    public Optional<Driver> findById(Integer driverId) {
        return drivers.findById(driverId);
    }

    @Override
    public List<Driver> findAllByCompany(Integer companyId) {
        return drivers.findAllByCompanyId(companyId);
    }
}


