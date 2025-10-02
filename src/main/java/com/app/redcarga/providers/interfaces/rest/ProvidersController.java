package com.app.redcarga.providers.interfaces.rest;

import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterCompanyCommand;
import com.app.redcarga.providers.domain.services.CompanyCommandService;
import com.app.redcarga.providers.interfaces.rest.requests.VerifyAndRegisterCompanyRequest;
import com.app.redcarga.providers.interfaces.rest.responses.VerifyAndRegisterCompanyResponse;
import com.app.redcarga.shared.infrastructure.security.AccountOwnershipGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProvidersController {

    private final CompanyCommandService service;
    private final AccountOwnershipGuard ownershipGuard;

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

        Integer companyId = service.handle(cmd);
        return ResponseEntity.ok(new VerifyAndRegisterCompanyResponse(true, companyId));
    }
}
