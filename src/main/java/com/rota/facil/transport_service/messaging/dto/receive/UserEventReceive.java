package com.rota.facil.transport_service.messaging.dto.receive;

import com.rota.facil.transport_service.domain.enums.Role;

import java.util.UUID;

public record UserEventReceive(
        UUID userId,
        UUID prefectureId,
        String name,
        String email,
        Role role
) {
}
