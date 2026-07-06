package com.rota.facil.transport_service.http.dto.request.user;

import java.util.UUID;

public record UpdateBusOfDriverRequestDTO(
        UUID driverId,
        UUID busId
) {
}
