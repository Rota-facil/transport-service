package com.rota.facil.transport_service.http.dto.response.user;

import java.util.UUID;

public record DriverBusResponseDTO(
        UUID id,
        UUID prefectureId,
        Long capacity,
        String plate
) {
}
