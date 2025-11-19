package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.queryservices.ChecklistDependencyQueryServiceImpl;
import com.app.redcarga.deals.domain.exceptions.ChecklistDependencyException;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstance;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceRepository;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.ChecklistDependencyQueryService;
import com.app.redcarga.deals.domain.services.ChecklistItemCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistItemCommandServiceImpl implements ChecklistItemCommandService {

    private final QuoteRepository quoteRepository;
    private final ChecklistInstanceRepository instanceRepository;
    private final ChecklistInstanceItemRepository itemRepository;
    private final ChecklistDependencyQueryService dependencyService;

    @Override
    @Transactional
    public void markItemDone(Integer quoteId, String itemCode, boolean checkDependencies, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));
        if (!"ACEPTADA".equals(quote.getStateCode())) throw new DomainException("quote_not_accepted");

        if (!instanceRepository.existsByQuoteId(quoteId)) throw new DomainException("checklist_instance_not_found");
        ChecklistInstance instance = instanceRepository.findByQuoteId(quoteId);

        if (checkDependencies) {
            List<String> deps = dependencyService.getDependsOn(instance.getTemplateId(), itemCode);
            if (deps != null && !deps.isEmpty()) {
                List<String> missing = dependencyService.findMissingDependencies(instance.getInstanceId(), deps);
                if (!missing.isEmpty()) throw new ChecklistDependencyException("checklist_missing_dependencies", missing);
            }
        }

        ChecklistInstanceItem item = itemRepository.findByInstanceIdAndCode(instance.getInstanceId(), itemCode)
                .orElseThrow(() -> new DomainException("checklist_item_assignment_missing"));

        item.setStatusCode("DONE");
        item.setCompletedBy(actorAccountId);
        item.setCompletedAt(Instant.now());
        itemRepository.save(item);
    }
}
