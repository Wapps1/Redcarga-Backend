package com.app.redcarga.identity.domain.exceptions;

import com.app.redcarga.shared.domain.exceptions.DomainException;

public class UnderagePersonException extends DomainException {
    public UnderagePersonException() { super("person must be 18+"); }
}
