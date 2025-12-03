package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceRepository;
import com.app.redcarga.deals.domain.services.ChecklistQueryService;
import com.app.redcarga.deals.interfaces.rest.responses.ChecklistItemResponse;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistQueryServiceImpl implements ChecklistQueryService {

    private final ChecklistInstanceRepository instanceRepo;
    private final ChecklistInstanceItemRepository itemRepo;

    @Override
    public List<ChecklistItemResponse> getChecklistItemsByQuoteId(Integer quoteId) {
        if (quoteId == null) return List.of();

        if (!instanceRepo.existsByQuoteId(quoteId)) return List.of();

        var instance = instanceRepo.findByQuoteId(quoteId);
        if (instance == null) return List.of();

        List<ChecklistInstanceItem> items = itemRepo.findAllByInstanceId(instance.getInstanceId());

        return items.stream().map(i -> new ChecklistItemResponse(
                i.getInstanceItemId(),
                i.getInstance().getInstanceId(),
                i.getCode(),
                i.getStatusCode(),
                i.getCompletedBy(),
                i.getCompletedAt()
        )).collect(Collectors.toList());
    }
}
