package com.rota.facil.transport_service.http.dto.response.trip;

import com.rota.facil.transport_service.domain.enums.Delay;
import com.rota.facil.transport_service.domain.enums.Progress;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripStatusResponseDTO(
        UUID id,
        Progress progress,
        Delay delay,
        LocalDateTime createdAt
) {
}
