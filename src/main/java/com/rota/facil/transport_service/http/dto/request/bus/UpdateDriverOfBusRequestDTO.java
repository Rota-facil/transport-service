package com.rota.facil.transport_service.http.dto.request.bus;

import java.util.UUID;

public record UpdateDriverOfBusRequestDTO(
        UUID driverId
) {
}
