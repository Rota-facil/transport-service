package com.rota.facil.transport_service.http.dto.request.tripUser;

import java.util.UUID;

public record TripUserUserResponseDTO(
        UUID id,
        String email
) {
}
