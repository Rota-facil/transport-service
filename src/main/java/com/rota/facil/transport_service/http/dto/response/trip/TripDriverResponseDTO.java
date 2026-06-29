package com.rota.facil.transport_service.http.dto.response.trip;

import java.util.UUID;

public record TripDriverResponseDTO(
        UUID id,
        String name,
        String email
) {
}
