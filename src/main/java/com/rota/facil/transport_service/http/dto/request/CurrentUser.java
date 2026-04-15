package com.rota.facil.transport_service.http.dto.request;

import java.util.UUID;

public record CurrentUser(
        UUID userId,
        UUID prefectureId,
        String email,
        String role
) {
}
