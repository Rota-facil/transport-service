package com.rota.facil.transport_service.http.dto.response.user;

import java.time.LocalDateTime;

public record EvaluateUserResponseDTO(
        String senderEmail,
        String receiverEmail,
        Double note,
        String feedback,
        LocalDateTime createdAt
) {
}
