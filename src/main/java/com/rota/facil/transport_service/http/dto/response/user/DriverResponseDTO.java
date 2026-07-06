package com.rota.facil.transport_service.http.dto.response.user;

import com.rota.facil.transport_service.domain.enums.DriverStatus;
import com.rota.facil.transport_service.domain.enums.Role;

import java.util.UUID;

public record DriverResponseDTO(
        UUID id,
        UUID prefectureId,
        String name,
        String email,
        Double score,
        Boolean active,
        DriverStatus status,
        Role role
) {
}
