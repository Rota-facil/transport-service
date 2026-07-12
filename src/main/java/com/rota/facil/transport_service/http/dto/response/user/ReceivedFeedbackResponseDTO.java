package com.rota.facil.transport_service.http.dto.response.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReceivedFeedbackResponseDTO(
        UUID id,
        String senderName,
        String senderEmail,
        Double note,
        String feedback,
        LocalDateTime createdAt
) {
}
