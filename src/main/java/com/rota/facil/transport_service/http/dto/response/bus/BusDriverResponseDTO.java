package com.rota.facil.transport_service.http.dto.response.bus;

import com.rota.facil.transport_service.domain.enums.Role;

import java.util.UUID;

public record BusDriverResponseDTO(
        UUID id,
        String name,
        String email,
        Role role
) {
}
