package com.app.redcarga.deals.application.internal.outboundservices.acl;

import com.app.redcarga.fleet.interfaces.acl.DriversFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriversFacadeClient {

    private final DriversFacade driversFacade;
    public boolean existsByIdAndAccountId(Integer accountId, Integer driverId) {
        return driversFacade.existsByIdAndAccountId(accountId, driverId);
    }
}
