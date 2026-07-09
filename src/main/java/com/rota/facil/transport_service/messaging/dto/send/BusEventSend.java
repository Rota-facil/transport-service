package com.rota.facil.transport_service.messaging.dto.send;

import java.util.UUID;

public record BusEventSend(
        UUID busId,
        UUID userId,
        UUID prefectureId,
        String role,
        String userEmail,
        String actionTitle,
        String actionType,
        String resourceName,
        UUID resourceId,
        String plate,
        Long capacity
) {
}
