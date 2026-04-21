package com.rota.facil.transport_service.http.dto.response.bus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BusResponseDTO(
        UUID id,
        UUID prefectureId,
        Long capacity,
        String plate,
        LocalDateTime createdAt,
        BusDriverResponseDTO driver
) {
}
