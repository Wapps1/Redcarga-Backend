package com.app.redcarga.planning.application.internal.queryservices;

import com.app.redcarga.planning.application.internal.gateways.RequestInboxQueryRepository;
import com.app.redcarga.planning.application.internal.views.RequestInboxEntryView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestInboxQueryServiceImpl implements RequestInboxQueryService {

    private final RequestInboxQueryRepository repo;

    @Override
    public List<RequestInboxEntryView> findByCompany(int companyId) {
        return repo.findByCompany(companyId);
    }
}