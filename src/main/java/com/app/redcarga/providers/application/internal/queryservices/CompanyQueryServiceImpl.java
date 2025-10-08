package com.app.redcarga.providers.application.internal.queryservices;

import com.app.redcarga.providers.application.internal.views.CompanyView;
import com.app.redcarga.providers.domain.queries.CompanyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryServiceImpl implements CompanyQueryService {

    private final CompanyQueryRepository repo;

    @Override
    public Optional<CompanyView> getCompany(int companyId) {
        return repo.findById(companyId);
    }
}