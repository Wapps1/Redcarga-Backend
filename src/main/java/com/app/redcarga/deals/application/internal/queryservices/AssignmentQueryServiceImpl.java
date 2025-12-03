package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.application.internal.outboundservices.acl.DriversFacadeClient;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.queries.AcceptedAssignmentInfo;
import com.app.redcarga.deals.domain.services.AssignmentQueryService;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import com.app.redcarga.requests.interfaces.acl.RequestFacade.RequestNameAndUbigeoSnapshot;
import com.app.redcarga.requests.domain.model.valueobjects.UbigeoSnapshot;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentQueryServiceImpl implements AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;
    private final ProvidersMembershipClient providersMembershipClient;
    private final DriversFacadeClient driversFacadeClient;
    private final QuoteQueryService quoteQueryService;
    private final RequestFacade requestFacade;

    @Override
    public Optional<Integer> getVersionByQuoteId(Integer quoteId) {
        return assignmentRepository.findByQuoteId(quoteId).map(a -> a.getVersion());
    }

    @Override
    public List<AcceptedAssignmentInfo> getAcceptedAssignmentsForDriver(int requesterAccountId) {

        var snapshot = driversFacadeClient
                .getDriverAccountByAccountId(requesterAccountId)
                .orElseThrow(() ->
                        new DomainException("driver_not_found"));

        // 2) Extraer driverId y companyId desde el record
        Integer driverId = snapshot.driverId();
        Integer companyId = snapshot.companyId();

        if (driverId == null || companyId == null) {
            throw new DomainException("driver_snapshot_invalid");
        }

        var roles = providersMembershipClient.getActiveRoleCodes(companyId, requesterAccountId);
        boolean isAdmin = roles.stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
        boolean isDriverRole = roles.stream().anyMatch(r -> r.equalsIgnoreCase("DRIVER"));

        if (!isAdmin) {
            if (!isDriverRole) throw new DomainException("role_not_allowed");
            boolean ok = driversFacadeClient.existsByIdAndAccountId(requesterAccountId, driverId);
            if (!ok) throw new DomainException("driver_mismatch");
        }

        // devuelve Assignments cuya Quote asociada está en "ACEPTADA"
        List<Assignment> assignments = assignmentRepository.findByDriverIdAndQuoteState(driverId, "ACEPTADA");
        List<AcceptedAssignmentInfo> out = new ArrayList<>(assignments.size());

        for (Assignment a : assignments) {
            Integer quoteId = a.getQuoteId();
            Integer requestId = null;
            String requestName = null;
            UbigeoSnapshot origin = null;
            UbigeoSnapshot destination = null;

            Optional<Quote> qOpt = quoteQueryService.getById(quoteId);
            if (qOpt.isPresent()) {
                requestId = qOpt.get().getRequestId();
            }

            if (requestId != null) {
                Optional<RequestNameAndUbigeoSnapshot> snap = requestFacade.getRequestName(requestId);
                if (snap.isPresent()) {
                    RequestNameAndUbigeoSnapshot s = snap.get();
                    requestName = s.requestName();
                    origin = s.origin();
                    destination = s.destination();
                }
            }

            out.add(new AcceptedAssignmentInfo(a, quoteId, requestId, requestName, origin, destination));
        }

        return out;
    }


    @Override
    public Optional<Assignment> getAssignmentByQuoteId(Integer quoteId) {
        return assignmentRepository.findByQuoteId(quoteId);
    }

}
