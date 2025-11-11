package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.domain.services.AssignmentQueryService;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentQueryServiceImpl implements AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;

    @Override
    public Optional<Integer> getVersionByQuoteId(Integer quoteId) {
        return assignmentRepository.findByQuoteId(quoteId).map(a -> a.getVersion());
    }
}
