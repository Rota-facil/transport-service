package com.rota.facil.transport_service.http.dto.response.client.intelligence;

import java.time.LocalDateTime;
import java.util.UUID;

public record RouteInterpretationResponseDTO(
        UUID id,
        UUID routeId,
        String routeInterpretation,
        LocalDateTime createdAt
) {
}
