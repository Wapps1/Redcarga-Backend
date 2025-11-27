package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.application.internal.outboundservices.acl.DriversFacadeClient;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.services.AssignmentQueryService;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentQueryServiceImpl implements AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;
    private final ProvidersMembershipClient providersMembershipClient;
    private final DriversFacadeClient driversFacadeClient;
    private final QuoteQueryService quoteQueryService;

    @Override
    public Optional<Integer> getVersionByQuoteId(Integer quoteId) {
        return assignmentRepository.findByQuoteId(quoteId).map(a -> a.getVersion());
    }

    @Override
    public List<Assignment> getAcceptedAssignmentsForDriver(int companyId, int driverId, int requesterAccountId) {
        var roles = providersMembershipClient.getActiveRoleCodes(companyId, requesterAccountId);
        boolean isAdmin = roles.stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
        boolean isDriverRole = roles.stream().anyMatch(r -> r.equalsIgnoreCase("DRIVER"));

        if (!isAdmin) {
            if (!isDriverRole) throw new DomainException("role_not_allowed");
            boolean ok = driversFacadeClient.existsByIdAndAccountId(requesterAccountId, driverId);
            if (!ok) throw new DomainException("driver_mismatch");
        }

        // devuelve sólo Assignments cuya Quote asociada está en "ACEPTADA"
        return assignmentRepository.findByDriverIdAndQuoteState(driverId, "ACEPTADA");
    }
}
