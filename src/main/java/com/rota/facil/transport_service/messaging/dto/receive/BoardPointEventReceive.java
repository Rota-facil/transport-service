package com.rota.facil.transport_service.messaging.dto.receive;

import java.util.UUID;

public record BoardPointEventReceive(
        UUID boardPointId,
        String name,
        String latitude,
        String longitude
) {
}
