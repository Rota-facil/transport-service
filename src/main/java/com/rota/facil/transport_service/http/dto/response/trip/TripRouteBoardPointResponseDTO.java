package com.rota.facil.transport_service.http.dto.response.trip;

import java.util.UUID;

public record TripRouteBoardPointResponseDTO(
        UUID id,
        String name,
        Double latitude,
        Double longitude
) {
}
