package com.app.redcarga.deals.application.internal.acl;

import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.shared.ws.auth.ChatSubscriptionVerifierPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatAccessServiceImpl implements ChatSubscriptionVerifierPort {

    private final ChatParticipantGateway chatParticipantGateway;
    private final QuoteRepository quoteRepository;

    @Override
    public boolean canSubscribeToQuote(int quoteId, int accountId) {
        var quote = quoteRepository.findById(quoteId).orElse(null);
        if (quote == null) return false;
        String s = quote.getStateCode();
        boolean stateOk = "TRATO".equals(s) || "EN_ESPERA".equals(s) || "ACEPTADA".equals(s);
        if (!stateOk) return false;
        return chatParticipantGateway.exists(quoteId, accountId);
    }
}
