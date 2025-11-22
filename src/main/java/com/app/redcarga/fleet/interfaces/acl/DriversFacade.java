package com.app.redcarga.fleet.interfaces.acl;

public interface DriversFacade {
    boolean existsByIdAndAccountId(Integer accountId, Integer driverId);
}
