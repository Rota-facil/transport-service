package com.rota.facil.transport_service.http.dto.response.institution;

import java.util.UUID;

public record InstitutionRouteCountResponseDTO(
        UUID institutionId,
        Long routeCount
) {
}
