package com.app.redcarga.deals.application.internal.acl;

import com.app.redcarga.deals.application.internal.outboundservices.acl.DriversFacadeClient;
import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import com.app.redcarga.shared.ws.auth.DriverQuoteAssignmentVerifierPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverQuoteAssignmentAccessServiceImpl implements DriverQuoteAssignmentVerifierPort {

    private final AssignmentRepository assignmentRepo;
    private final DriversFacadeClient driversClient;

    @Override
    public boolean isDriverOfQuote(int quoteId, int accountId) {
        // 1. Buscar assignment por quoteId
        Optional<Assignment> assignment = assignmentRepo.findByQuoteId(quoteId);
        if (assignment.isEmpty()) return false;

        // 2. Obtener driverId del assignment
        Integer driverId = assignment.get().getDriverId();

        // 3. Verificar que accountId pertenece a ese driver
        return driversClient.existsByIdAndAccountId(accountId, driverId);
    }

}