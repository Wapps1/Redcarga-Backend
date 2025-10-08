package com.app.redcarga.providers.interfaces.rest;

import com.app.redcarga.providers.application.internal.outboundservices.acl.IamAccountProviderService;
import com.app.redcarga.providers.application.internal.queryservices.CompanyQueryService;
import com.app.redcarga.providers.application.internal.views.CompanyView;
import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterCompanyCommand;
import com.app.redcarga.providers.domain.queries.CompanyQueryRepository;
import com.app.redcarga.providers.domain.services.CompanyCommandService;
import com.app.redcarga.providers.interfaces.rest.requests.VerifyAndRegisterCompanyRequest;
import com.app.redcarga.providers.interfaces.rest.responses.VerifyAndRegisterCompanyResponse;
import com.app.redcarga.shared.infrastructure.security.AccountOwnershipGuard;
import com.app.redcarga.shared.infrastructure.security.TokenClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProvidersController {

    private final CompanyCommandService commandService;
    private final AccountOwnershipGuard ownershipGuard;

    private final CompanyQueryService queryService;
    private final CompanyQueryRepository queryRepo;
    private final TokenClaims claims;
    private final IamAccountProviderService iam;

    @SecurityRequirement(name="firebase")
    @Operation(summary = "Verifica y registra la compañía (estado SUBMITTED)")
    @PostMapping("/company/verify-and-register")
    public ResponseEntity<VerifyAndRegisterCompanyResponse> verifyAndRegister(
            @Valid @RequestBody VerifyAndRegisterCompanyRequest body
    ) {
        // Ownership: token Firebase vs accountId del body
        ownershipGuard.assertOwnershipOrThrow(body.accountId());

        var cmd = new VerifyAndRegisterCompanyCommand(
                body.accountId(),
                body.legalName(),
                body.tradeName(),
                body.ruc(),
                body.email(),
                body.phone(),
                body.address()
        );

        Integer companyId = commandService.handle(cmd);
        return ResponseEntity.ok(new VerifyAndRegisterCompanyResponse(true, companyId));
    }

    @SecurityRequirement(name = "iam")
    @Operation(summary = "Get company information by id")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyView> getCompany(@PathVariable int companyId) {
        var opt = queryService.getCompany(companyId);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        var view = opt.get();

        int actor = resolveActorAccountIdOrThrow();
        boolean allowed = (view.createdByAccountId() == actor) || queryRepo.hasActiveMembership(companyId, actor);
        if (!allowed) throw new AccessDeniedException("not_a_member_of_company");

        CompanyView response = new CompanyView(
                view.companyId(), view.legalName(), view.tradeName(), view.ruc(),
                view.email(), view.phone(), view.address(), view.status(),
                view.docsStatus(), view.createdByAccountId(), view.membersCount()
        );
        return ResponseEntity.ok(response);
    }
    private int resolveActorAccountIdOrThrow() {
        return claims.accountIdClaim().orElseGet(() ->
                claims.uid().flatMap(iam::getByExternalUid)
                        .or(() -> claims.email().flatMap(iam::getByEmail))
                        .map(s -> s.accountId())
                        .orElseThrow(() -> new AccessDeniedException("account_not_found"))
        );
    }
}
