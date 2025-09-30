package com.app.redcarga.iam.application.internal.eventhandlers;

import com.app.redcarga.iam.application.internal.integration.contracts.ClientKycPassed;
import com.app.redcarga.iam.application.internal.integration.contracts.ProviderKycPassed;
import com.app.redcarga.iam.application.internal.integration.contracts.ProviderOnboarded;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Instant;

@Service
public class KycEventsHandler {

    private final SignupIntentRepository signupIntentRepository;
    private static final Logger log = LoggerFactory.getLogger(KycEventsHandler.class);

    public KycEventsHandler(SignupIntentRepository signupIntentRepository) {
        this.signupIntentRepository = signupIntentRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onClientKycPassed(ClientKycPassed e) {
        var intent = loadOpenIntent(e.accountId());

        log.info("[IAM] before: {}", intent.getStatus());

        intent.complete(Instant.now()); // → DONE

        log.info("[IAM] after : {}", intent.getStatus());
        signupIntentRepository.save(intent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProviderKycPassed(ProviderKycPassed e) {
        SignupIntent intent = loadOpenIntent(e.accountId());
        intent.markBasicProfileCompleted(Instant.now()); // → BASIC_PROFILE_COMPLETED
        signupIntentRepository.save(intent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProviderOnboarded(ProviderOnboarded e) {
        SignupIntent intent = loadOpenIntent(e.accountId());
        intent.complete(Instant.now()); // BASIC_PROFILE_COMPLETED → DONE
        signupIntentRepository.save(intent);
    }

    private SignupIntent loadOpenIntent(Integer accountId) {
        return signupIntentRepository.findOpenByAccountId(accountId)
                .orElseThrow(() -> new IllegalStateException("No open SignupIntent for account " + accountId));
    }
}
