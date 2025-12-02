package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.model.commands.SendUserChatMessageCommand;
import com.app.redcarga.deals.domain.services.ChatCommandService;
import com.app.redcarga.deals.domain.services.ChatQueryService;
import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.interfaces.rest.requests.SendChatMessageRequest;
import com.app.redcarga.deals.interfaces.rest.requests.MarkChatReadRequest;
import com.app.redcarga.deals.interfaces.rest.responses.SendChatMessageResponse;
import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import com.app.redcarga.deals.interfaces.rest.responses.ChatHistoryResponse;
import com.app.redcarga.deals.interfaces.rest.responses.ChatListResponse;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deals/quotes/{quoteId}/chat")
@SecurityRequirement(name = "iam")
@RequiredArgsConstructor
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;
    private final ChatParticipantGateway chatParticipantGateway;
    private final ChatMessageGateway chatMessageGateway;

    @PostMapping("/messages")
    public ResponseEntity<SendChatMessageResponse> sendMessage(@PathVariable Integer quoteId,
                                                               @Valid @RequestBody SendChatMessageRequest body,
                                                               JwtAuthenticationToken principal) {
        Integer actor = Integer.valueOf(principal.getToken().getSubject());
        if (!chatParticipantGateway.exists(quoteId, actor)) {
            throw new DomainException("not_chat_participant");
        }
        UUID dedup = parseDedup(body.dedupKey());
        var cmd = new SendUserChatMessageCommand(
                quoteId,
                actor,
                dedup,
                body.kind(),
                body.text(),
                body.url(),
                body.caption()
        );
        Integer id = chatCommandService.sendUserMessage(cmd);
        var dto = chatMessageGateway.findAfter(quoteId, id - 1, 1).get(0);
        return ResponseEntity.ok(new SendChatMessageResponse(true, id, dto.createdAt()));
    }

    @GetMapping
    public ResponseEntity<ChatHistoryResponse> history(@PathVariable Integer quoteId,
                                                       JwtAuthenticationToken principal) {
        Integer actor = Integer.valueOf(principal.getToken().getSubject());
        var response = chatQueryService.getFullHistory(quoteId, actor);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read")
    public ResponseEntity<Void> markRead(@PathVariable Integer quoteId,
                                         @Valid @RequestBody MarkChatReadRequest body,
                                         JwtAuthenticationToken principal) {
        Integer actor = Integer.valueOf(principal.getToken().getSubject());
        chatCommandService.markRead(quoteId, actor, body.lastSeenMessageId());
        return ResponseEntity.ok().build();
    }

    private UUID parseDedup(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return UUID.fromString(raw.trim()); }
        catch (IllegalArgumentException e) { throw new DomainException("dedup_invalid"); }
    }


    /*
    @GetMapping("/list")
    public ResponseEntity<ChatListResponse> listChats(JwtAuthenticationToken principal) {
        Integer actor = Integer.valueOf(principal.getToken().getSubject());
        boolean isProvider = principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PROVIDER".equals(a.getAuthority()));

        var response = chatQueryService.listChats(actor, isProvider);
        return ResponseEntity.ok(response);
    }
     */
}

