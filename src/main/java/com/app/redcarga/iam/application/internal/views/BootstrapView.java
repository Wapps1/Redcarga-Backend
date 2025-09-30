package com.app.redcarga.iam.application.internal.views;

public record BootstrapView(
        MeView me,
        RegistrationView registration,
        FeaturesView features,
        String serverTime // ISO-8601 UTC
) {
    public record MeView(
            Integer accountId,
            String externalUid,
            String email,
            String username,
            String role,          // CLIENT|PROVIDER|PLATFORM_ADMIN
            boolean emailVerified
    ) { }

    public record RegistrationView(
            String basicProfile,      // MISSING|DONE   (por ahora DONE hasta que exista Identity)
            String providerOnboarding,// NOT_STARTED|IN_PROGRESS|READY_FOR_REVIEW|APPROVED
            String nextStep           // VERIFY_EMAIL|COMPLETE_BASIC_PROFILE|START_PROVIDER_ONBOARDING|NONE
    ) { }

    public record FeaturesView(
            boolean mfaEnforced
    ) { }
}
