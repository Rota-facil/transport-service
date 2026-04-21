package com.rota.facil.transport_service.http.dto.request.tripUser;

import java.util.UUID;

public record TripUserBoardPointResponseDTO(
        UUID id,
        String name,
        String latitude,
        String longitude
) {
}
