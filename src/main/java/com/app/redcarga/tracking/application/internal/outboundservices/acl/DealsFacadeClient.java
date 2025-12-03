package com.app.redcarga.tracking.application.internal.outboundservices.acl;

import com.app.redcarga.deals.interfaces.acl.DealsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealsFacadeClient {

    private final DealsFacade dealsFacade;

    public boolean isChatParticipant(Integer quoteId, Integer accountId) {
        return dealsFacade.isChatParticipant(quoteId, accountId);
    }

    public boolean isDriverOfQuote(Integer quoteId, Integer accountId) {
        return dealsFacade.isDriverOfQuote(quoteId, accountId);
    }

    public Optional<Integer> findDriverIdByQuoteId(Integer quoteId) {
        return dealsFacade.findDriverIdByQuoteId(quoteId);
    }
}