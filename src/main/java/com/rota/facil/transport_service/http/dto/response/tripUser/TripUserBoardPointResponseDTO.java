package com.rota.facil.transport_service.http.dto.response.tripUser;

import java.util.UUID;

public record TripUserBoardPointResponseDTO(
        UUID id,
        String name,
        Double latitude,
        Double longitude
) {
}
