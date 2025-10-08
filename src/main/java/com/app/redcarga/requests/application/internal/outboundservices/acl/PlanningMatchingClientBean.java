package com.app.redcarga.requests.application.internal.outboundservices.acl;

import com.app.redcarga.planning.interfaces.acl.PlanningMatchingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service("requestsPlanningMatchingClientBean")
@RequiredArgsConstructor
public class PlanningMatchingClientBean implements PlanningMatchingClient {

    private final PlanningMatchingFacade planning;

    @Override
    public void matchAndNotify(int requestId,
                               String oDep, String oProv,
                               String dDep, String dProv,
                               Instant createdAt,
                               String requesterNameSnapshot,
                               String originDepartmentName,
                               String originProvinceName,
                               String destDepartmentName,
                               String destProvinceName,
                               Integer totalQuantity) {

        var cmd = new PlanningMatchingFacade.PlanningMatchCommand(
                requestId,
                // Códigos (orden correcto)
                oDep,
                dDep,
                oProv,
                dProv,
                createdAt,
                requesterNameSnapshot,
                // Nombres y totales
                originDepartmentName,
                originProvinceName,
                destDepartmentName,
                destProvinceName,
                totalQuantity
        );

        planning.matchAndNotify(cmd);
    }
}
