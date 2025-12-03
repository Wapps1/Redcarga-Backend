package com.app.redcarga.deals.application.internal.acl;

import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import com.app.redcarga.deals.application.internal.outboundservices.acl.DriversFacadeClient;
import com.app.redcarga.deals.interfaces.acl.DealsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealsFacadeImpl implements DealsFacade {

    private final QuoteRepository quoteRepository;
    private final ChatParticipantGateway chatParticipantGateway;
    private final AssignmentRepository assignmentRepo;
    private final DriversFacadeClient driversClient;

    @Override
    public boolean isChatParticipant(Integer quoteId, Integer accountId) {
        if (quoteId == null || accountId == null) return false;
        var quote = quoteRepository.findById(quoteId).orElse(null);
        if (quote == null) return false;
        String s = quote.getStateCode();
        boolean stateOk = "TRATO".equals(s) || "EN_ESPERA".equals(s) || "ACEPTADA".equals(s);
        if (!stateOk) return false;
        return chatParticipantGateway.exists(quoteId, accountId);
    }

    @Override
    public boolean isDriverOfQuote(Integer quoteId, Integer accountId) {
        if (quoteId == null || accountId == null) return false;
        Optional<Assignment> assignment = assignmentRepo.findByQuoteId(quoteId);
        if (assignment.isEmpty()) return false;
        Integer driverId = assignment.get().getDriverId();
        return driversClient.existsByIdAndAccountId(accountId, driverId);
    }

    @Override
    public Optional<Integer> findDriverIdByQuoteId(Integer quoteId) {
        if (quoteId == null) return Optional.empty();
        return assignmentRepo.findByQuoteId(quoteId).map(Assignment::getDriverId);
    }
}