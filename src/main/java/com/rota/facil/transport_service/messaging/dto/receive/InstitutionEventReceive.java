package com.rota.facil.transport_service.messaging.dto.receive;

import java.util.UUID;

public record InstitutionEventReceive(
        UUID institutionId,
        String name,
        String latitude,
        String longitude
) {
}