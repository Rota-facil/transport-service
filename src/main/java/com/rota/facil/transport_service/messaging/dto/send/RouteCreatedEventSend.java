package com.rota.facil.transport_service.messaging.dto.send;

import java.util.UUID;

public record RouteCreatedEventSend(
        UUID userId,
        String role,
        String userEmail,
        String actionTitle,
        String actionType,
        String resourceName,
        UUID resourceId,
        UUID routeId,
        String routeName
) {
}
