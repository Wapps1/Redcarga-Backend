package com.app.redcarga.providers.domain.model.entities;

import com.app.redcarga.providers.domain.model.aggregates.CompanyMember;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyMemberRoleId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "providers", name = "company_member_roles")
public class CompanyMemberRole {

    @EmbeddedId
    private CompanyMemberRoleId id;

    // FK -> company_roles(role_id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "role_id",
            nullable = false,
            insertable = false, updatable = false,    // <- evita duplicar columna del EmbeddedId
            foreignKey = @ForeignKey(name = "fk_cmr_role")
    )
    private CompanyRole role;

    // FK -> company_members(company_id, account_id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(
                    name = "company_id", referencedColumnName = "company_id", nullable = false,
                    insertable = false, updatable = false, // <- evita duplicado
                    foreignKey = @ForeignKey(name = "fk_cmr_member_company")
            ),
            @JoinColumn(
                    name = "account_id", referencedColumnName = "account_id", nullable = false,
                    insertable = false, updatable = false, // <- evita duplicado
                    foreignKey = @ForeignKey(name = "fk_cmr_member_account")
            )
    })
    private CompanyMember member;

    /** Factory: asigna un rol a un miembro (ADMIN, etc.) */
    public static CompanyMemberRole grant(CompanyMember member, CompanyRole role) {
        if (member == null) throw new IllegalArgumentException("member_required");
        if (role == null || role.getRoleId() == null) throw new IllegalArgumentException("role_required");

        CompanyMemberRole cmr = new CompanyMemberRole();
        cmr.member = member;
        cmr.role = role;
        cmr.id = new CompanyMemberRoleId(
                member.getId().getCompanyId(),
                member.getId().getAccountId(),
                role.getRoleId()
        );
        return cmr;
    }
}
