package com.rota.facil.transport_service.http.dto.response.trip;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TripResponseDTO(
        UUID id,
        String name,
        String reasonOfCancellation,
        Long students,
        Double latitude,
        Double longitude,
        LocalDate createdAt,
        TripBusResponseDTO bus,
        TripRouteResponseDTO route,
        String actualStatus,
        List<TripStatusResponseDTO> tripStatus
) {
}
