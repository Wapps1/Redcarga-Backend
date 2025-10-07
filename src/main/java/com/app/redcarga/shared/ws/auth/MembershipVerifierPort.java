package com.app.redcarga.shared.ws.auth;

/** Puerto genérico: Providers u otro BC provee el adapter real. */
public interface MembershipVerifierPort {
    boolean isActiveMember(int companyId, int accountId);
}
