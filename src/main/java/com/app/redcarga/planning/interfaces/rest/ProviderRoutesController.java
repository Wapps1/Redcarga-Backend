package com.app.redcarga.planning.interfaces.rest;

import com.app.redcarga.planning.domain.services.ProviderRouteCommandService;
import com.app.redcarga.planning.domain.model.commands.RegisterProviderRouteCommand;
import com.app.redcarga.planning.interfaces.rest.requests.RegisterProviderRouteRequest;
import com.app.redcarga.planning.interfaces.rest.responses.RegisterProviderRouteResponse;
import com.app.redcarga.shared.infrastructure.security.TokenClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class ProviderRoutesController {

    private final ProviderRouteCommandService service;
    private final TokenClaims claims;

    @PostMapping("/companies/{companyId}/routes")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Registrar una ruta (DD o PP) para la compañía indicada")
    public ResponseEntity<RegisterProviderRouteResponse> register(
            @PathVariable int companyId,
            @Valid @RequestBody RegisterProviderRouteRequest req
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        String op = emptyToNull(req.originProvinceCode());
        String dp = emptyToNull(req.destProvinceCode());

        var cmd = new RegisterProviderRouteCommand(
                companyId,
                req.routeTypeId(),
                null, 
                req.originDepartmentCode(),
                req.destDepartmentCode(),
                op,
                dp,
                req.active()
        );

        Integer id = service.register(cmd, actorAccountId);
        return ResponseEntity.ok(new RegisterProviderRouteResponse(true, id));
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
