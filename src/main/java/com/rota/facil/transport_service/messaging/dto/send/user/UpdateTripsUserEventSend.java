package com.rota.facil.transport_service.messaging.dto.send.user;

import java.util.List;
import java.util.UUID;

public record UpdateTripsUserEventSend(
        List<UUID> userIds
) {
}
