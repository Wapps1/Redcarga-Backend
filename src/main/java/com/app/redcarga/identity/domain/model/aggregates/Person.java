package com.app.redcarga.identity.domain.model.aggregates;

import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.identity.domain.model.valueobjects.BirthDate;
import com.app.redcarga.identity.domain.model.valueobjects.FullName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "identity", name = "persons",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_person_doc", columnNames = {"doc_type_id", "doc_number"}),
                @UniqueConstraint(name = "uq_person_account", columnNames = {"account_id"})
        },
        indexes = {
                @Index(name = "ix_persons_doc_type", columnList = "doc_type_id")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "person_id")),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", nullable = false))
})
public class Person extends AuditableAbstractAggregateRoot<Person> {

    @Column(name = "account_id", nullable = false, unique = true)
    private Integer accountId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // FK a identity.doc_types
    @Column(name = "doc_type_id", nullable = false)
    private Integer docTypeId;

    @Column(name = "doc_number", nullable = false, length = 32)
    private String docNumber;

    public Person(Integer accountId,
                  FullName fullName,
                  BirthDate birthDate,
                  Integer docTypeId,
                  String docNumber) {
        if (accountId == null) throw new IllegalArgumentException("accountId required");
        if (docTypeId == null) throw new IllegalArgumentException("docTypeId required");
        if (docNumber == null || docNumber.isBlank()) throw new IllegalArgumentException("docNumber required");
        if (!birthDate.isAdult(18)) throw new com.app.redcarga.identity.domain.exceptions.UnderagePersonException();

        this.accountId = accountId;
        this.fullName = fullName.toString();
        this.birthDate = birthDate.toLocalDate();
        this.docTypeId = docTypeId;
        this.docNumber = docNumber.trim();
    }
}
