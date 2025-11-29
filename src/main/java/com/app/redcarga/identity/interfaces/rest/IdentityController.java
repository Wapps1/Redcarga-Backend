package com.app.redcarga.identity.interfaces.rest;

import com.app.redcarga.identity.domain.model.commands.VerifyAndCreatePersonCommand;
import com.app.redcarga.identity.domain.model.aggregates.Person;
import com.app.redcarga.identity.domain.services.PersonCommandService;
import com.app.redcarga.identity.domain.services.PersonQueryService;
import com.app.redcarga.identity.interfaces.rest.responses.IdentityPersonResponse;
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
    private final PersonQueryService personQueryService;
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
                req.birthDate(),
                req.phone(),
                req.ruc()
        );

        var person = personCommands.handle(cmd);

        return ResponseEntity.ok(new VerifyAndCreateResponse(true, person.getId()));
    }

    @SecurityRequirement(name="iam")
    @GetMapping("/{accountId}")
    public ResponseEntity<IdentityPersonResponse> getByAccountId(@PathVariable("accountId") Integer accountId) {
        return personQueryService.findByAccountId(accountId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private IdentityPersonResponse toResponse(Person p) {
        return new IdentityPersonResponse(
                p.getId(),
                p.getAccountId(),
                p.getFullName(),
                p.getBirthDate(),
                p.getDocTypeId(),
                p.getDocNumber(),
                p.getPhone(),
                p.getRuc()
        );
    }
}
