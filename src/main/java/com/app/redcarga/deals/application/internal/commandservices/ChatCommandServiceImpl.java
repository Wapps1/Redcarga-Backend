package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.application.internal.gateways.ChatReadGateway;
import com.app.redcarga.deals.domain.model.commands.SendUserChatMessageCommand;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.ChatCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatCommandServiceImpl implements ChatCommandService {

    private final ChatMessageGateway chatMessageGateway;
    private final ChatParticipantGateway chatParticipantGateway;
    private final ChatReadGateway chatReadGateway;
    private final QuoteRepository quoteRepository;
    private final com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter chatOutboxAdapter;

    @Override
    @Transactional
    public Integer sendUserMessage(SendUserChatMessageCommand cmd) {
        var quote = quoteRepository.findById(cmd.quoteId())
                .orElseThrow(() -> new DomainException("quote_not_found"));

        // Estado permitido
        if (!isChatEnabledState(quote.getStateCode())) {
            throw new DomainException("chat_state_not_allowed");
        }

        // Participación
        if (!chatParticipantGateway.exists(cmd.quoteId(), cmd.actorAccountId())) {
            throw new DomainException("not_chat_participant");
        }

        // Validaciones kind & contenido
        String contentCode = mapKind(cmd.kind());
        String body;
        String mediaUrl;
        if ("TEXT".equals(contentCode)) {
            body = normalizeText(cmd.text());
            mediaUrl = null;
        } else if ("IMAGE".equals(contentCode)) {
            mediaUrl = requireImageUrl(cmd.imageUrl());
            body = normalizeCaption(cmd.caption());
        } else {
            throw new DomainException("content_kind_invalid");
        }

        // Idempotencia por dedupKey
        if (cmd.dedupKey() != null) {
            var existing = chatMessageGateway.findByDedupKey(cmd.quoteId(), cmd.dedupKey());
            if (existing.isPresent()) return existing.get().messageId();
        }

        int id = chatMessageGateway.insertUserMessage(
            cmd.quoteId(), contentCode, body, mediaUrl, cmd.dedupKey(), cmd.actorAccountId()
        );

        // Snapshot del mensaje para outbox (en la misma transacción)
        var dto = chatMessageGateway.findAfter(cmd.quoteId(), id - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) {
            chatOutboxAdapter.persistUserMessageOutbox(dto);
        }
        return id;
    }

    @Override
    @Transactional
    public void markRead(Integer quoteId, Integer actorAccountId, Integer lastSeenMessageId) {
        if (!chatParticipantGateway.exists(quoteId, actorAccountId)) {
            throw new DomainException("not_chat_participant");
        }
        chatReadGateway.upsertLastSeen(quoteId, actorAccountId, lastSeenMessageId);
    }

    private boolean isChatEnabledState(String state) {
        return switch (state) {
            case "TRATO", "EN_ESPERA", "ACEPTADA" -> true;
            default -> false;
        };
    }

    private String mapKind(String raw) {
        if (raw == null) throw new DomainException("kind_required");
        return switch (raw.toUpperCase()) {
            case "TEXT" -> "TEXT";
            case "IMAGE" -> "IMAGE";
            default -> throw new DomainException("kind_invalid");
        };
    }

    private String normalizeText(String t) {
        if (t == null) throw new DomainException("text_required");
        t = t.trim();
        if (t.isEmpty()) throw new DomainException("text_empty");
        if (t.length() > 4000) throw new DomainException("text_too_long");
        return t;
    }

    private String requireImageUrl(String u) {
        if (u == null || u.isBlank()) throw new DomainException("image_url_required");
        u = u.trim();
        if (u.length() > 1000) throw new DomainException("image_url_too_long");
        return u;
    }

    private String normalizeCaption(String c) {
        if (c == null || c.isBlank()) return null;
        c = c.trim();
        if (c.length() > 4000) throw new DomainException("caption_too_long");
        return c;
    }
}
