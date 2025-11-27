package com.app.redcarga.fleet.domain.services;

import com.app.redcarga.fleet.interfaces.rest.responses.DriverView;

import java.util.List;
import java.util.Optional;

public interface DriverQueryService {
    Optional<DriverView> findById(Integer driverId);

    // devuelve drivers mapeados a DriverView
    List<DriverView> findAllByCompany(Integer companyId);

    // sobrecarga usada para validar que un accountId pertenece a la compañía
    List<DriverView> findAllByCompany(Integer companyId, Integer accountId);

    boolean existsByIdAndAccountId(Integer accountId, Integer driverId);
}


