package com.app.redcarga.requests.domain.services;

import com.app.redcarga.requests.domain.model.commands.CloseRequestCommand;
import com.app.redcarga.requests.domain.model.commands.CreateRequestCommand;

public interface RequestCommandService {
    Integer create(CreateRequestCommand cmd);
    void close(CloseRequestCommand cmd);
}
