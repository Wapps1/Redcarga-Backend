package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.application.internal.outboundservices.acl.CompaniesCatalogClient;
import com.app.redcarga.deals.application.internal.outboundservices.acl.IdentityAccountClient;
import com.app.redcarga.deals.domain.services.ChatQueryService;
import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import com.app.redcarga.deals.interfaces.rest.responses.ChatHistoryResponse;
import com.app.redcarga.deals.interfaces.rest.responses.ChatListResponse;
import com.app.redcarga.deals.interfaces.rest.responses.ChatListItemDto;
import com.app.redcarga.identity.interfaces.acl.IdentityPersonSnapshot;
import com.app.redcarga.providers.application.internal.views.CompanyNamesView;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatQueryServiceImpl implements ChatQueryService {

    private final ChatMessageGateway chatMessageGateway;
    private final ChatParticipantGateway chatParticipantGateway;
    private final CompaniesCatalogClient companiesCatalogClient;
    private final IdentityAccountClient identityAccountClient;

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ChatMessageDto> getMessages(Integer quoteId, Integer afterId, Integer limit, Integer actorAccountId) {
        if (!chatParticipantGateway.exists(quoteId, actorAccountId)) {
            throw new DomainException("not_chat_participant");
        }
        if (limit == null || limit < 1 || limit > 200) throw new DomainException("limit_invalid");
        return chatMessageGateway.findAfter(quoteId, afterId == null ? 0 : afterId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatHistoryResponse getFullHistory(Integer quoteId, Integer actorAccountId) {
        if (!chatParticipantGateway.exists(quoteId, actorAccountId)) {
            throw new DomainException("not_chat_participant");
        }
        
        var messages = chatMessageGateway.findAll(quoteId);
        var lastRead = chatMessageGateway.getLastReadMessageId(quoteId, actorAccountId);
        
        return new ChatHistoryResponse(lastRead, messages);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatListResponse listChats(Integer userId, boolean isProvider) {
        var quoteIds = chatParticipantGateway.findQuoteIdsByUserId(userId);
        var items = new ArrayList<ChatListItemDto>();

        for (Integer quoteId : quoteIds) {
            var participants = chatParticipantGateway.findParticipantsByQuoteId(quoteId);
            
            // Find the "other" participant (not current user)
            var otherParticipant = participants.stream()
                .filter(p -> !p.userId().equals(userId))
                .findFirst()
                .orElse(null);

            if (otherParticipant == null) continue; // Skip if no other participant

            Integer otherUserId = otherParticipant.userId();
            Integer otherCompanyId = otherParticipant.companyId();
            
            String companyLegalName = null;
            String companyTradeName = null;
            String personFullName = null;

            // If current user is PROVIDER, show client person info
            // If current user is CLIENT, show provider company info
            if (isProvider) {
                // Provider sees client's full name
                var personOpt = identityAccountClient.findByAccountId(otherUserId);
                if (personOpt.isPresent() && personOpt.get() instanceof IdentityPersonSnapshot snapshot) {
                    personFullName = snapshot.fullName();
                }
            } else {
                // Client sees provider's company names
                var companyOpt = companiesCatalogClient.getCompanyNames(otherCompanyId);
                if (companyOpt.isPresent()) {
                    CompanyNamesView company = companyOpt.get();
                    companyLegalName = company.legalName();
                    companyTradeName = company.tradeName();
                }
            }

            // Calculate unread count
            int lastRead = chatMessageGateway.getLastReadMessageId(quoteId, userId);
            int unread = chatMessageGateway.countAfter(quoteId, lastRead);

            items.add(new ChatListItemDto(
                quoteId,
                otherUserId,
                otherCompanyId,
                companyLegalName,
                companyTradeName,
                personFullName,
                unread
            ));
        }

        return new ChatListResponse(items);
    }
}
