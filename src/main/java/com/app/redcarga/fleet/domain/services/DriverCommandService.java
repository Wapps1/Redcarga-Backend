package com.app.redcarga.fleet.domain.services;

import com.app.redcarga.fleet.domain.model.commands.CreateDriverCommand;
import com.app.redcarga.fleet.domain.model.commands.UpdateDriverCommand;
import com.app.redcarga.fleet.domain.model.commands.DeleteDriverCommand;

public interface DriverCommandService {
    Integer handle(CreateDriverCommand cmd);
    void handle(UpdateDriverCommand cmd);
    void handle(DeleteDriverCommand cmd);
}


