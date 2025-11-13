package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.domain.services.ChecklistInstanceCommandService;
import com.app.redcarga.deals.infrastructure.persistence.jdbc.ChecklistTemplateJdbcRepository;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstance;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistInstanceCommandServiceImpl implements ChecklistInstanceCommandService {

    private final ChecklistTemplateJdbcRepository templateJdbc;
    private final ChecklistInstanceRepository instanceRepo;
    private final ChecklistInstanceItemRepository itemRepo;

    @Override
    @Transactional
    public void createForAcceptedQuote(Integer quoteId) {
        if (quoteId == null) throw new IllegalArgumentException("quoteId_required");
        if (instanceRepo.existsByQuoteId(quoteId)) return;

        var templateOpt = templateJdbc.findDefaultTemplate();
        var template = templateOpt.orElseThrow(() -> new IllegalStateException("default_checklist_template_missing"));

        try {
            ChecklistInstance toCreate = new ChecklistInstance();
            toCreate.setQuoteId(quoteId);
            toCreate.setTemplateId(template.templateId());
            toCreate.setCreatedAt(Instant.now());
            ChecklistInstance saved = instanceRepo.save(toCreate);

            List<ChecklistInstanceItem> items = templateJdbc.findTemplateItems(template.templateId()).stream()
                    .map(ti -> {
                        var it = new ChecklistInstanceItem();
                        it.setInstance(saved);
                        it.setCode(ti.code());
                        it.setStatusCode("PENDING");
                        return it;
                    }).collect(Collectors.toList());

            if (!items.isEmpty()) itemRepo.insertAll(items);
        } catch (DataIntegrityViolationException ex) {
            // race: another tx created the instance — treat as OK (idempotent)
        }
    }
}
