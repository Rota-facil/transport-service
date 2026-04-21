package com.rota.facil.transport_service.http.dto.response.trip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TripResponseDTO(
        UUID id,
        String reasonOfCancellation,
        LocalDateTime createdAt,
        TripBusResponseDTO bus,
        TripRouteResponseDTO route,
        List<TripStatusResponseDTO> tripStatus
) {
}
