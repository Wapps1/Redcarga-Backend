package com.app.redcarga.deals.domain.model.aggregates;

import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.deals.domain.model.valueobjects.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "deals", name = "quote")
@AttributeOverride(name = "id", column = @Column(name = "quote_id"))
public class Quote extends AuditableAbstractAggregateRoot<Quote> {

    @Column(name = "request_id", nullable = false)
    private Integer requestId;

    @Column(name = "provider_id", nullable = false)
    private Integer providerId;

    @Column(name = "created_by_account_id", nullable = false)
    private Integer createdByAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", length = 3, nullable = false)
    private Currency currency = Currency.PEN;

    @Column(name = "state_code", length = 32, nullable = false)
    private String stateCode;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // Factory
    public static Quote create(CreateQuoteCommand cmd, Integer creatorAccountId) {
        if (cmd == null) throw new IllegalArgumentException("create_quote_command_required");
        if (creatorAccountId == null) throw new IllegalArgumentException("creator_account_required");
        if (cmd.requestId() == null || cmd.requestId() <= 0) throw new IllegalArgumentException("requestId_invalid");
        if (cmd.providerId() == null || cmd.providerId() <= 0) throw new IllegalArgumentException("providerId_invalid");
        if (cmd.totalAmount() == null || cmd.totalAmount().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("totalAmount_invalid");

        Quote q = new Quote();
        q.requestId = cmd.requestId();
        q.providerId = cmd.providerId();
        q.createdByAccountId = creatorAccountId;
        q.totalAmount = cmd.totalAmount();
        q.currency = cmd.currency() == null ? Currency.PEN : cmd.currency();
        q.stateCode = "PENDIENTE";
        return q;
    }
}
