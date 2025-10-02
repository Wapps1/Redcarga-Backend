package com.app.redcarga.providers.domain.model.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "providers", name = "company_roles",
        uniqueConstraints = @UniqueConstraint(name = "uq_company_roles_code", columnNames = "code"))
public class CompanyRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "code", nullable = false, length = 40)
    private String code; // p.ej., ADMIN

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    public static CompanyRole of(String code, String name) {
        CompanyRole r = new CompanyRole();
        r.code = code;
        r.name = name;
        return r;
    }
}
