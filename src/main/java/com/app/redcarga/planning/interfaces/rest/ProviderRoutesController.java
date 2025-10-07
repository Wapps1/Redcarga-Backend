package com.app.redcarga.planning.interfaces.rest;

import com.app.redcarga.planning.application.internal.outboundservices.acl.ProvidersMembershipService;
import com.app.redcarga.planning.application.internal.queryservices.ProviderRoutesQueryService;
import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
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

import java.util.List;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class ProviderRoutesController {

    private final ProviderRouteCommandService commandService;
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

        Integer id = commandService.register(cmd, actorAccountId);
        return ResponseEntity.ok(new RegisterProviderRouteResponse(true, id));
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }


    private final ProviderRoutesQueryService queryService;
    private final ProvidersMembershipService membership;

    @GetMapping("/providers/{companyId}/routes")
    @Operation(summary = "List provider routes",
            description = "Devuelve las rutas publicadas por la empresa indicada. Filtros opcionales de shape y ubicación.")
    public ResponseEntity<List<ProviderRouteView>> listProviderRoutes(
            @PathVariable int companyId
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("missing_account_id_claim"));

        if (!membership.isMemberOfCompany(companyId, actorAccountId)) {
            throw new AccessDeniedException("not_a_member_of_company");
        }

        var result = queryService.listRoutes(companyId);
        return ResponseEntity.ok(result);
    }
}
