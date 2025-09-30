package com.app.redcarga.identity.domain.model.valueobjects;

import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.Period;

@EqualsAndHashCode
public final class BirthDate {
    private final LocalDate value;

    public BirthDate(LocalDate value) {
        if (value == null) throw new IllegalArgumentException("birthDate required");
        if (value.isAfter(LocalDate.now())) throw new IllegalArgumentException("birthDate in future");
        this.value = value;
    }

    public boolean isAdult(int minYears) {
        return Period.between(value, LocalDate.now()).getYears() >= minYears;
    }

    public LocalDate toLocalDate() { return value; }
}
