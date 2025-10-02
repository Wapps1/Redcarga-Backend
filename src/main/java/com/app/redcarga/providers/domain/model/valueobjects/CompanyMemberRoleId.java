package com.app.redcarga.providers.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
public class CompanyMemberRoleId implements Serializable {
    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "role_id")
    private Integer roleId;

    protected CompanyMemberRoleId() {}
    public CompanyMemberRoleId(Integer companyId, Integer accountId, Integer roleId) {
        this.companyId = companyId; this.accountId = accountId; this.roleId = roleId;
    }
    public Integer getCompanyId() { return companyId; }
    public Integer getAccountId() { return accountId; }
    public Integer getRoleId() { return roleId; }
}