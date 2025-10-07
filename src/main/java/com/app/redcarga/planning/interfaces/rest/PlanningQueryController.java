package com.app.redcarga.planning.interfaces.rest;

import com.app.redcarga.planning.application.internal.outboundservices.acl.ProvidersMembershipService;
import com.app.redcarga.planning.application.internal.queryservices.ProviderRoutesQueryService;
import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import com.app.redcarga.shared.infrastructure.security.TokenClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
public class PlanningQueryController {

    private final ProviderRoutesQueryService service;
    private final ProvidersMembershipService membership;
    private final TokenClaims claims;

    @GetMapping("/providers/{companyId}/routes")
    @Operation(summary = "List provider routes",
            description = "Devuelve las rutas publicadas por la empresa indicada. Filtros opcionales de shape y ubicación.")
    @SecurityRequirement(name = "bearerAuth")
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

        var data = service.listRoutes(companyId, shape, active,
                originDepartmentCode, originProvinceCode,
                destDepartmentCode, destProvinceCode);

        return ResponseEntity.ok(data);
    }
}
