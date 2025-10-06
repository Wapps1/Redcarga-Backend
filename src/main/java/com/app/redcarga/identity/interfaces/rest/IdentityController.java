package com.app.redcarga.identity.interfaces.rest;

import com.app.redcarga.identity.domain.model.commands.VerifyAndCreatePersonCommand;
import com.app.redcarga.identity.domain.services.PersonCommandService;
import com.app.redcarga.shared.infrastructure.security.AccountOwnershipGuard;
import com.app.redcarga.identity.interfaces.rest.requests.VerifyAndCreateRequest;
import com.app.redcarga.identity.interfaces.rest.responses.VerifyAndCreateResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final PersonCommandService personCommands;
    private final AccountOwnershipGuard ownershipGuard;

    @SecurityRequirement(name="firebase")
    @PostMapping("/verify-and-create")
    public ResponseEntity<VerifyAndCreateResponse> verifyAndCreate(@Valid @RequestBody VerifyAndCreateRequest req) {
        ownershipGuard.assertOwnershipOrThrow(req.accountId());

        var cmd = new VerifyAndCreatePersonCommand(
                req.accountId(),
                req.fullName(),
                req.docTypeCode(),
                req.docNumber(),
                req.birthDate()
        );

        var person = personCommands.handle(cmd);

        return ResponseEntity.ok(new VerifyAndCreateResponse(true, person.getId()));
    }
}
