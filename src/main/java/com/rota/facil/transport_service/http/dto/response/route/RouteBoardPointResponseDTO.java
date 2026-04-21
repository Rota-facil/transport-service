package com.rota.facil.transport_service.http.dto.response.route;

import java.time.LocalTime;
import java.util.UUID;

public record RouteBoardPointResponseDTO(
        UUID id,
        String name,
        String latitude,
        String longitude,
        LocalTime boardTimeGoing,
        LocalTime boardTimeFinish
) {
}
