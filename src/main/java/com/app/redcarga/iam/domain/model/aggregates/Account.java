package com.app.redcarga.iam.domain.model.aggregates;

import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.iam.domain.model.valueobjects.AccountStatus;
import com.app.redcarga.iam.domain.model.valueobjects.RoleCode;
import com.app.redcarga.iam.domain.model.events.AccountRegistered;
import com.app.redcarga.iam.domain.model.events.EmailVerified;
import com.app.redcarga.shared.domain.model.valueobjects.Email;
import com.app.redcarga.iam.domain.model.valueobjects.ExternalUid;
import com.app.redcarga.iam.domain.model.valueobjects.Username;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "iam", name = "accounts")
@AttributeOverride(name = "id", column = @Column(name = "account_id"))
public class Account extends AuditableAbstractAggregateRoot<Account> {

    @Column(name = "external_uid", nullable = false, unique = true)
    private String externalUid;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // Catálogo de roles: guardamos el ID; el código lo resolverás por join si lo necesitas
    @Column(name = "system_role_id", nullable = false)
    private Integer systemRoleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    public Account(ExternalUid externalUid, Email email, Username username, Integer systemRoleId) {
        this.externalUid = externalUid.toString();
        this.email = email.toString();
        this.username = username.toString();
        this.systemRoleId = systemRoleId;
        // evento de alta
        addDomainEvent(new AccountRegistered(getId(), this.email, String.valueOf(RoleCode.CLIENT))); // ajusta si quieres el code real
    }

    public void markEmailVerified() {
        if (this.emailVerified) return;
        this.emailVerified = true;
        addDomainEvent(new EmailVerified(getId(), this.email));
    }

    public void lock() { this.status = AccountStatus.LOCKED; }

    public void delete() { this.status = AccountStatus.DELETED; }
}
