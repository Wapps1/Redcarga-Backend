package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteCommandServiceImpl implements QuoteCommandService {

    private final QuoteRepository quoteRepository;
    private final ProvidersMembershipClient providersMembershipClient;

    @Override
    @Transactional
    public Integer create(CreateQuoteCommand cmd, Integer creatorAccountId) {
        // Validación básica adicional a la del aggregate (ej. membership provider)
        // Asumimos que providerId en la quote se refiere a companyId (si fuera distinto, ajustar naming)
        if (!providersMembershipClient.isMemberOfCompany(cmd.providerId(), creatorAccountId)) {
            throw new IllegalStateException("not_member_of_company");
        }
        Quote quote = Quote.create(cmd, creatorAccountId);
        quoteRepository.save(quote);
        return quote.getId();
    }
}
