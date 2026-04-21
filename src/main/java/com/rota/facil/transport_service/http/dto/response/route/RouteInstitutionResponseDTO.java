package com.rota.facil.transport_service.http.dto.response.route;

import java.util.UUID;

public record RouteInstitutionResponseDTO(
        UUID id,
        String name,
        String latitude,
        String longitude
) {
}
