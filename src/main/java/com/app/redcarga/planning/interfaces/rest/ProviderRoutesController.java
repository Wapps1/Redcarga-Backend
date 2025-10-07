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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class ProviderRoutesController {

    private final ProviderRouteCommandService service;
    private final TokenClaims claims;

    @PostMapping("/companies/{companyId}/routes")
    @Operation(summary = "Registrar una ruta (DD o PP) para la compañía indicada")
    public ResponseEntity<RegisterProviderRouteResponse> register(
            @PathVariable int companyId,
            @Valid @RequestBody RegisterProviderRouteRequest req
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        // Normaliza strings vacíos a null para provincias (para no forzar al cliente a enviar null)
        String op = emptyToNull(req.originProvinceCode());
        String dp = emptyToNull(req.destProvinceCode());

        var cmd = new RegisterProviderRouteCommand(
                companyId,
                req.routeTypeId(),
                null, // shape: la app lo resuelve desde el catálogo por routeTypeId
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


    private final ProviderRoutesQueryService services;
    private final ProvidersMembershipService membership;

    @GetMapping("/providers/{companyId}/routes")
    @Operation(summary = "List provider routes",
            description = "Devuelve las rutas publicadas por la empresa indicada. Filtros opcionales de shape y ubicación.")
    public ResponseEntity<List<ProviderRouteView>> listProviderRoutes(
            @PathVariable int companyId,
            @RequestParam(required = false) String shape,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String originDepartmentCode,
            @RequestParam(required = false) String originProvinceCode,
            @RequestParam(required = false) String destDepartmentCode,
            @RequestParam(required = false) String destProvinceCode
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("missing_account_id_claim"));

        if (!membership.isMemberOfCompany(companyId, actorAccountId)) {
            throw new AccessDeniedException("not_a_member_of_company");
        }

        var data = services.listRoutes(companyId, shape, active,
                originDepartmentCode, originProvinceCode,
                destDepartmentCode, destProvinceCode);

        return ResponseEntity.ok(data);
    }
}
