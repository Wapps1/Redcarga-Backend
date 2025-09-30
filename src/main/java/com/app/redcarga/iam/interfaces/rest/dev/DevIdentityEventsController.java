package com.app.redcarga.iam.interfaces.rest.dev;

import com.app.redcarga.iam.application.internal.eventhandlers.KycEventsHandler;
import com.app.redcarga.iam.application.internal.integration.contracts.ClientKycPassed;
import com.app.redcarga.iam.application.internal.integration.contracts.ProviderKycPassed;
import com.app.redcarga.iam.application.internal.integration.contracts.ProviderOnboarded;
import com.app.redcarga.iam.interfaces.acl.IamAccountFacade;
import com.app.redcarga.iam.interfaces.acl.IamAccountSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ConditionalOnProperty(name = "stubs.enabled", havingValue = "true")
@RestController
@RequestMapping("/_dev")
@Tag(name = "Dev Identity Events", description = "Stubs para simular eventos de Identity/Providers")
public class DevIdentityEventsController {

    private final KycEventsHandler handler;
    private final IamAccountFacade facade;

    public DevIdentityEventsController(KycEventsHandler handler, IamAccountFacade facade) {
        this.facade = facade;
        this.handler = handler;
    }

    @Operation(summary = "CLIENT KYC passed → DONE")
    @PostMapping("/identity/client-kyc-passed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clientKycPassed(@Valid @RequestBody ClientKycPassed payload) {
        handler.onClientKycPassed(payload);
    }

    @Operation(summary = "PROVIDER KYC passed → BASIC_PROFILE_COMPLETED")
    @PostMapping("/identity/provider-kyc-passed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void providerKycPassed(@Valid @RequestBody ProviderKycPassed payload) {
        handler.onProviderKycPassed(payload);
    }

    @Operation(summary = "Provider onboarded → DONE")
    @PostMapping("/providers/onboarded")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void providerOnboarded(@Valid @RequestBody ProviderOnboarded payload) {
        handler.onProviderOnboarded(payload);
    }

    @Operation(summary = "Snapshot de IAM por accountId")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<IamAccountSnapshot> getSnapshot(@PathVariable int accountId) {
        return facade.findByAccountId(accountId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
