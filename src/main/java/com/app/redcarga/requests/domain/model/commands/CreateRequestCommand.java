package com.app.redcarga.requests.domain.model.commands;

import com.app.redcarga.requests.domain.model.valueobjects.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateRequestCommand(
        Integer requesterAccountId,
        String requesterNameSnapshot,
        UbigeoSnapshot origin,
        UbigeoSnapshot destination,
        Integer itemsCount,
        BigDecimal totalWeightKg,
        List<CreateRequestItemCommand> items
) {}
