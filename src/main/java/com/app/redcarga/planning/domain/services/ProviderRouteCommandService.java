package com.app.redcarga.planning.domain.services;

import com.app.redcarga.planning.domain.model.commands.RegisterProviderRouteCommand;
import com.app.redcarga.planning.domain.model.commands.UpdateProviderRouteCommand;
import com.app.redcarga.planning.domain.model.commands.DeleteProviderRouteCommand;

public interface ProviderRouteCommandService {
    /** Caso de uso “autoprotegido”: requiere el actor para validar rol ADMIN en la company. */
    Integer register(RegisterProviderRouteCommand cmd, int actorAccountId);

    /** Actualiza una ruta existente; mantiene la validación de rol ADMIN. */
    void update(UpdateProviderRouteCommand cmd, int actorAccountId);

    /** Elimina una ruta existente; mantiene la validación de rol ADMIN. */
    void delete(DeleteProviderRouteCommand cmd, int actorAccountId);
}
