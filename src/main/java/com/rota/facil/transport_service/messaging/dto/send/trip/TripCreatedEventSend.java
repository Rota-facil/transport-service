package com.rota.facil.transport_service.messaging.dto.send.trip;

import java.util.UUID;

public record TripCreatedEventSend(
        UUID tripId,
        UUID prefectureId,
        UUID routeId,
        String routeName
) {
}
