package com.rota.facil.transport_service.http.dto.request.trip;

import java.util.UUID;

public record CreateTripRequestDTO(
        UUID routeId,
        UUID busId
) {
}
