package com.app.redcarga.fleet.application.internal.acl;

import com.app.redcarga.fleet.domain.model.queries.DriverAccountInfo;
import com.app.redcarga.fleet.domain.services.DriverQueryService;
import com.app.redcarga.fleet.interfaces.acl.DriversFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriversFacadeImpl implements DriversFacade {

    private final DriverQueryService driverQueryService;

    @Override
    public boolean existsByIdAndAccountId(Integer accountId, Integer driverId) {
        return driverQueryService.existsByIdAndAccountId(accountId, driverId);
    }

    @Override
    public Optional<DriverAccountSnapshot> getDriverAccountByAccountId(Integer accountId) {
        return driverQueryService.getDriverAccountInfoByAccountId(accountId)
                .map(i -> new DriverAccountSnapshot(i.driverId(), i.companyId()));
    }
}