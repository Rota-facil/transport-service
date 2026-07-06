package com.rota.facil.transport_service.http.dto.response.user;

import com.rota.facil.transport_service.domain.enums.BusStatus;

import java.util.UUID;

public record DriverBusResponseDTO(
        UUID id,
        UUID prefectureId,
        Long capacity,
        String plate,
        BusStatus status
) {
}
