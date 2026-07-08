package com.rota.facil.transport_service.http.dto.response.route;

import java.util.UUID;

public record RouteBusResponseDTO(
        UUID id,
        String plate
) {
}
