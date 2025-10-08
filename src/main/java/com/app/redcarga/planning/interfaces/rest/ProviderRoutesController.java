package com.app.redcarga.planning.interfaces.rest;

import com.app.redcarga.planning.application.internal.outboundservices.acl.ProvidersMembershipService;
import com.app.redcarga.planning.application.internal.queryservices.ProviderRoutesQueryService;
import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import com.app.redcarga.planning.domain.services.ProviderRouteCommandService;
import com.app.redcarga.planning.domain.model.commands.RegisterProviderRouteCommand;
import com.app.redcarga.planning.interfaces.rest.requests.RegisterProviderRouteRequest;
import com.app.redcarga.planning.interfaces.rest.requests.UpdateProviderRouteRequest;
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
    @Operation(summary = "Register a route (DD or PP) for the specified company")
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
            description = "Returns the routes published by the specified company.")
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

    @GetMapping("/companies/{companyId}/routes/{routeId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Get a provider route by id",
            description = "Returns the specific route for the given company. Requires company membership.")
    public ResponseEntity<ProviderRouteView> getProviderRoute(
            @PathVariable int companyId,
            @PathVariable int routeId
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("missing_account_id_claim"));

        if (!membership.isMemberOfCompany(companyId, actorAccountId)) {
            throw new AccessDeniedException("not_a_member_of_company");
        }

        return queryService.findByCompanyAndId(companyId, routeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/companies/{companyId}/routes/{routeId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update a provider route")
    public ResponseEntity<Void> updateProviderRoute(
            @PathVariable int companyId,
            @PathVariable int routeId,
            @Valid @RequestBody UpdateProviderRouteRequest req
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        var cmd = new com.app.redcarga.planning.domain.model.commands.UpdateProviderRouteCommand(
                routeId,
                companyId,
                req.routeTypeId(),
                req.originDepartmentCode(),
                req.destDepartmentCode(),
                emptyToNull(req.originProvinceCode()),
                emptyToNull(req.destProvinceCode()),
                req.active()
        );
        commandService.update(cmd, actorAccountId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/companies/{companyId}/routes/{routeId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Delete a provider route")
    public ResponseEntity<Void> deleteProviderRoute(
            @PathVariable int companyId,
            @PathVariable int routeId
    ) {
        int actorAccountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        var cmd = new com.app.redcarga.planning.domain.model.commands.DeleteProviderRouteCommand(
                routeId,
                companyId
        );
        commandService.delete(cmd, actorAccountId);
        return ResponseEntity.noContent().build();
    }
}
