package com.app.redcarga.identity.domain.exceptions;

import com.app.redcarga.shared.domain.exceptions.DomainException;

public class InvalidDocumentFormatException extends DomainException {
    public InvalidDocumentFormatException(String msg) { super(msg); }
}
