package com.rota.facil.transport_service.http.dto.request.route;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRouteRecurringBusRequestDTO(
        @NotNull(message = "ônibus é obrigatório")
        UUID busId
) {
}
