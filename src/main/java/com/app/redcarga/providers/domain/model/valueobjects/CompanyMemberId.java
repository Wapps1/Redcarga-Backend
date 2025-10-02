package com.app.redcarga.providers.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Getter
@EqualsAndHashCode
@Embeddable
public class CompanyMemberId implements Serializable {
    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "account_id")
    private Integer accountId;

    protected CompanyMemberId() {}
    public CompanyMemberId(Integer companyId, Integer accountId) {
        this.companyId = companyId; this.accountId = accountId;
    }
    public Integer getCompanyId() { return companyId; }
    public Integer getAccountId() { return accountId; }
}