package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.app.redcarga.shared.domain.exceptions.DomainException;

@Service
@RequiredArgsConstructor
public class QuoteCommandServiceImpl implements QuoteCommandService {

    private final QuoteRepository quoteRepository;
    private final ProvidersMembershipClient providersMembershipClient;

    @Override
    @Transactional
    public Integer create(CreateQuoteCommand cmd, Integer creatorAccountId) {
        // Validación: el creador debe pertenecer a la compañía
        if (!providersMembershipClient.isMemberOfCompany(cmd.companyId(), creatorAccountId)) {
            throw new DomainException("not_member_of_company");
        }
        Quote quote = Quote.create(cmd, creatorAccountId);
        quoteRepository.save(quote);
        return quote.getId();
    }

    @Override
    @Transactional
    public void updateItemQty(Integer quoteId, Integer requestItemId, java.math.BigDecimal qty, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        // membership check: actor must belong to the quote's company
        if (!providersMembershipClient.isMemberOfCompany(quote.getCompanyId(), actorAccountId)) {
            throw new DomainException("not_member_of_company");
        }
        quote.updateItemQty(requestItemId, qty);
        quoteRepository.save(quote);
    }

    @Override
    @Transactional
    public void removeItem(Integer quoteId, Integer requestItemId, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        if (!providersMembershipClient.isMemberOfCompany(quote.getCompanyId(), actorAccountId)) {
            throw new DomainException("not_member_of_company");
        }
        quote.removeItem(requestItemId);
        quoteRepository.save(quote);
    }
}
