package com.rota.facil.transport_service.http.dto.request.trip;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTripRequestDTO(
        @NotNull(message = "rota é obrigatória")
        UUID routeId,

        @NotNull(message = "ônibus é obrigatória")
        UUID busId
) {
}
