package com.rota.facil.transport_service.messaging.dto.send;

import java.util.UUID;

public record FeedbackUserEventSend(
        UUID userId,
        double note
) {
}
