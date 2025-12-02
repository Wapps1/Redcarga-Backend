package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.ChatQueryService;
import com.app.redcarga.deals.interfaces.rest.responses.ChatListResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deals/chat")
@SecurityRequirement(name = "iam")
@RequiredArgsConstructor
public class ChatListController {

    private final ChatQueryService chatQueryService;

    @GetMapping("/list")
    public ResponseEntity<ChatListResponse> listChats(JwtAuthenticationToken principal) {
        Integer actor = Integer.valueOf(principal.getToken().getSubject());
        boolean isProvider = principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PROVIDER".equals(a.getAuthority()));

        var response = chatQueryService.listChats(actor, isProvider);
        return ResponseEntity.ok(response);
    }
}
