package com.rota.facil.transport_service.http.dto.request.route;

import com.rota.facil.transport_service.domain.enums.Shift;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record CreateRouteRequestDTO(
    @NotNull(message = "turno é obrigatório")
    Shift shift,

    @NotNull(message = "horário de ida é obrigatório")
    LocalTime going,

    @NotNull(message = "horário de volta é obrigatório")
    LocalTime return_,

    @NotNull(message = "horário de finalizaçao de ida é obrigatório")
    LocalTime goingFinish,

    @NotNull(message = "horário de finalização de volta é obrigatório")
    LocalTime returnFinish,

    @NotNull(message = "selecione pelo menos uma instiuiçao é obrigátorio")
    Set<UUID> institutionsIds,

    @NotNull(message = "recorrencia é obrigatória")
    CreateRouteRecurringRequestDTO recurring
) {
}
