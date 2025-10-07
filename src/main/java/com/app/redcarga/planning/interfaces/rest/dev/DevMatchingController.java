package com.app.redcarga.planning.interfaces.rest.dev;

import com.app.redcarga.planning.application.internal.commandservices.RequestMatchingService;
import com.app.redcarga.planning.application.internal.outboundservices.notifications.NotificationsPort; // <-- puerto
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/_dev/planning")
@RequiredArgsConstructor
@Validated
public class DevMatchingController {

    private final RequestMatchingService service;
    private final NotificationsPort notifications; // <-- inyección

    @PostMapping("/match")
    public ResponseEntity<DevMatchResponse> match(@Valid @RequestBody DevMatchRequest req) {
        String oProv = normalize(req.originProvinceCode());
        String dProv = normalize(req.destProvinceCode());
        var createdAt = req.createdAt() != null ? req.createdAt() : Instant.now();
        boolean dry = req.dryRun() == null ? true : req.dryRun();

        service.matchAndNotify(
                req.requestId(),
                oProv,
                req.originDepartmentCode(),
                dProv,
                req.destDepartmentCode(),
                createdAt,
                dry,
                req.requesterName()
        );

        return ResponseEntity.ok(new DevMatchResponse(
                true, dry, createdAt,
                "Si hay coincidencias, se publicaron notificaciones WS AFTER_COMMIT."
        ));
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // ===== DTOs =====

    public record DevMatchRequest(
            int requestId,
            @NotBlank @Pattern(regexp = "\\d{2}") String originDepartmentCode,
            @Pattern(regexp = "\\d{4}") String originProvinceCode, // opcional (PP)
            @NotBlank @Pattern(regexp = "\\d{2}") String destDepartmentCode,
            @Pattern(regexp = "\\d{4}") String destProvinceCode,   // opcional (PP)
            Instant createdAt,
            Boolean dryRun, // null -> true
            @NotBlank String requesterName
    ) {}

    public record DevMatchResponse(
            boolean accepted,
            boolean dryRun,
            Instant effectiveCreatedAt,
            String note
    ) {}

    // ===== Cierre “solo WS” (no BD) =====

    @PostMapping("/close")
    public ResponseEntity<CloseResp> close(@RequestBody CloseReq req) {
        // Modo prueba: solo WS, no toca la BD
        // Así no desaparece nada del inbox porque estas requests son ficticias
        notifications.notifyRequestClosed(req.requestId(), req.companyId());
        return ResponseEntity.ok(new CloseResp(true, "REQUEST_CLOSED enviado por WS (sin tocar BD)."));
    }

    public record CloseReq(int requestId, int companyId) {}
    public record CloseResp(boolean ok, String note) {}
}
