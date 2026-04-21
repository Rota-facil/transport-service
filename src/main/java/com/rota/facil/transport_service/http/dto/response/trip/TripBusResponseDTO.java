package com.rota.facil.transport_service.http.dto.response.trip;

import java.util.UUID;

public record TripBusResponseDTO(
        UUID id,
        UUID prefectureId,
        Long capacity,
        String plate
) {
}
