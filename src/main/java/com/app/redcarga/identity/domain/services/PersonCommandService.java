package com.app.redcarga.identity.domain.services;

import com.app.redcarga.identity.domain.model.aggregates.Person;
import com.app.redcarga.identity.domain.model.commands.VerifyAndCreatePersonCommand;

public interface PersonCommandService {
    Person handle(VerifyAndCreatePersonCommand command);
}
