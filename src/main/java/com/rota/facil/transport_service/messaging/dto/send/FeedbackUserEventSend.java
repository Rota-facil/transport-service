package com.rota.facil.transport_service.messaging.dto.send;

import java.util.UUID;

public record FeedbackUserEventSend(
        UUID userId,
        double note,
        UUID senderId,
        String sender,
        UUID receiverId,
        String receiver,
        String feedback,
        UUID resourceId,
        String userEmail,
        String role,
        String actionTitle,
        String actionType,
        String resourceName
) {
}
