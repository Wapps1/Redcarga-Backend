package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import com.app.redcarga.deals.domain.repositories.ChangeRepository;
import com.app.redcarga.deals.domain.repositories.ChangeItemRepository;
import com.app.redcarga.deals.domain.services.ChangeQueryService;
import com.app.redcarga.deals.interfaces.rest.responses.ChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangeQueryServiceImpl implements ChangeQueryService {

    private final ChangeRepository changeRepository;
    private final ChangeItemRepository changeItemRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ChangeResponse> getChangeWithItems(Integer changeId) {
        Optional<Change> change = changeRepository.findById(changeId);
        if (change.isEmpty()) return Optional.empty();

        List<ChangeItem> items = changeItemRepository.findByChangeId(changeId);
        return Optional.of(ChangeResponse.from(change.get(), items));
    }
}