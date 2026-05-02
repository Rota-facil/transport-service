package com.rota.facil.transport_service.http.dto.response.route;

import java.time.LocalTime;
import java.util.UUID;

public record RouteBoardPointResponseDTO(
        UUID id,
        String name,
        Double latitude,
        Double longitude,
        LocalTime boardTimeGoing,
        LocalTime boardTimeFinish
) {
}
