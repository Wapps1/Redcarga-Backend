package com.app.redcarga.fleet.interfaces.acl;

import java.util.Optional;

/** Facade público para consultas de drivers */
public interface DriversFacade {
    boolean existsByIdAndAccountId(Integer accountId, Integer driverId);

    // Nuevo: snapshot público driverId + companyId
    record DriverAccountSnapshot(Integer driverId, Integer companyId) {}

    Optional<DriverAccountSnapshot> getDriverAccountByAccountId(Integer accountId);
}
