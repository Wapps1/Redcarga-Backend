package com.app.redcarga.tracking.application.internal.outboundservices.acl;

import com.app.redcarga.fleet.interfaces.acl.DriversFacade;
import com.app.redcarga.fleet.interfaces.acl.DriversFacade.DriverAccountSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriversTrackingFacadeClient {

    private final DriversFacade driversFacade;

    // Devuelve el snapshot completo si lo necesitas
    public Optional<DriverAccountSnapshot> getDriverAccountByAccountId(Integer accountId) {
        return driversFacade.getDriverAccountByAccountId(accountId);
    }

    // Método de conveniencia: sólo el driverId (útil para tracking)
    public Optional<Integer> getDriverIdByAccountId(Integer accountId) {
        return driversFacade.getDriverAccountByAccountId(accountId)
                .map(DriverAccountSnapshot::driverId);
    }
}