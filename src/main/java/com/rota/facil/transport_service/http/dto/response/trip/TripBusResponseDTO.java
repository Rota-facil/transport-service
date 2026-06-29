package com.rota.facil.transport_service.http.dto.response.trip;

import java.util.UUID;

public record TripBusResponseDTO(
        UUID id,
        TripDriverResponseDTO driver,
        UUID prefectureId,
        Long capacity,
        String plate
) {
}
