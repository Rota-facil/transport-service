package com.rota.facil.transport_service.http.dto.request.trip;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JoinUserInTrip(
        @NotNull(message = "ponto de embarque é obrigatório")
        UUID boardPointId,

        @NotNull(message = "instituição é obrigatório")
        UUID institutionId,

        @NotNull(message = "marcar opção de ida é obrigatório")
        Boolean going,

        @NotNull(message = "marcar opção de volta é obrigatório")
        Boolean return_
) {
}
