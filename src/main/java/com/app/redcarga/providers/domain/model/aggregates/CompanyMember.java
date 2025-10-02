package com.app.redcarga.providers.domain.model.aggregates;

import com.app.redcarga.providers.domain.model.valueobjects.MembershipStatus;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyMemberId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "providers", name = "company_members")
public class CompanyMember {

    @EmbeddedId
    private CompanyMemberId id;

    @MapsId("companyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_company_members_company"))
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private MembershipStatus status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    private CompanyMember(Company company, Integer accountId) {
        this.company = company;
        this.id = new CompanyMemberId(company.getId(), accountId);
        this.status = MembershipStatus.ACTIVE;
        this.joinedAt = Instant.now();
    }

    public static CompanyMember joinAsActive(Company company, Integer accountId) {
        if (company == null) throw new IllegalArgumentException("company_required");
        if (accountId == null || accountId <= 0) throw new IllegalArgumentException("accountId_invalid");
        return new CompanyMember(company, accountId);
    }

    public void remove() { this.status = MembershipStatus.REMOVED; }
}
