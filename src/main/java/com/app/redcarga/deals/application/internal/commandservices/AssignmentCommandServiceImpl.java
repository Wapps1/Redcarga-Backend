package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstance;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.repositories.AssignmentRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceRepository;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.AssignmentCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentCommandServiceImpl implements AssignmentCommandService {

    private final QuoteRepository quoteRepository;
    private final AssignmentRepository assignmentRepository;
    private final ChecklistInstanceRepository instanceRepository;
    private final ChecklistInstanceItemRepository itemRepository;

    private static final String ASSIGNMENT_CODE = "ASSIGNMENT_SET";

    @Override
    @Transactional
    public void assignToQuote(Integer quoteId, Integer driverId, Integer vehicleId, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));
        if (!"ACEPTADA".equals(quote.getStateCode())) throw new DomainException("quote_not_accepted");

        // create assignment
        Assignment a = new Assignment();
        a.setQuoteId(quoteId);
        a.setDriverId(driverId);
        a.setVehicleId(vehicleId);
        a.setAssignedBy(actorAccountId);
        Assignment saved = assignmentRepository.save(a);

        // ensure checklist instance exists
        if (!instanceRepository.existsByQuoteId(quoteId)) throw new DomainException("checklist_instance_not_found");
        ChecklistInstance instance = instanceRepository.findByQuoteId(quoteId);

        Optional<ChecklistInstanceItem> optItem = itemRepository.findByInstanceIdAndCode(instance.getInstanceId(), ASSIGNMENT_CODE);
        if (optItem.isPresent()) {
            ChecklistInstanceItem it = optItem.get();
            it.setStatusCode("DONE");
            it.setCompletedBy(actorAccountId);
            it.setCompletedAt(Instant.now());
            it.setAssignmentId(saved.getAssignmentId());
            itemRepository.save(it);
        } else {
            throw new DomainException("checklist_item_assignment_missing");
        }
    }

    @Override
    @Transactional
    public void unassignFromQuote(Integer quoteId, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));
        if (!"ACEPTADA".equals(quote.getStateCode())) throw new DomainException("quote_not_accepted");

        assignmentRepository.findByQuoteId(quoteId).ifPresent(a -> assignmentRepository.deleteById(a.getAssignmentId()));

        if (!instanceRepository.existsByQuoteId(quoteId)) throw new DomainException("checklist_instance_not_found");
        ChecklistInstance instance = instanceRepository.findByQuoteId(quoteId);

        Optional<ChecklistInstanceItem> optItem = itemRepository.findByInstanceIdAndCode(instance.getInstanceId(), ASSIGNMENT_CODE);
        if (optItem.isPresent()) {
            ChecklistInstanceItem it = optItem.get();
            it.setStatusCode("PENDING");
            it.setCompletedBy(null);
            it.setCompletedAt(null);
            it.setAssignmentId(null);
            itemRepository.save(it);
        }
    }
}
