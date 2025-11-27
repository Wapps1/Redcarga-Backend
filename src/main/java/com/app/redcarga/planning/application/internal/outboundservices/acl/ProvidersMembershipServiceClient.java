package com.app.redcarga.planning.application.internal.outboundservices.acl;

import com.app.redcarga.providers.interfaces.acl.ProvidersMembershipFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvidersMembershipServiceClient implements ProvidersMembershipService {

    private final ProvidersMembershipFacade providers;

    @Override
    public boolean isMemberOfCompany(int companyId, int accountId) {
        return providers.isMemberOfCompany(companyId, accountId);
    }

    @Override
    public boolean hasAnyRole(int companyId, int accountId, List<Integer> roleCodes) {
        List<Integer> codes = (roleCodes == null)
                ? List.of()
                : List.copyOf(roleCodes);

        int[] arr = codes.stream().mapToInt(Integer::intValue).toArray();

        return providers.hasAnyRole(companyId, accountId, arr);
    }

}
