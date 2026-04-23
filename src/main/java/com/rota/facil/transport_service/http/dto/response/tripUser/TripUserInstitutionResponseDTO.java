package com.rota.facil.transport_service.http.dto.response.tripUser;

import java.util.UUID;

public record TripUserInstitutionResponseDTO(
        UUID id,
        String name,
        String latitude,
        String longitude
) {
}
