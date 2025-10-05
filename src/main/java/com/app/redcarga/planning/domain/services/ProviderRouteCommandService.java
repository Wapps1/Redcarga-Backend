package com.app.redcarga.planning.domain.services;

import com.app.redcarga.planning.domain.model.commands.RegisterProviderRouteCommand;

public interface ProviderRouteCommandService {
    /** Caso de uso “autoprotegido”: requiere el actor para validar rol ADMIN en la company. */
    Integer register(RegisterProviderRouteCommand cmd, int actorAccountId);
}
