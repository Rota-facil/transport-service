package com.rota.facil.transport_service.persistence.dto;

import java.util.UUID;

public record StudentPersistenceDTO(
        UUID id,
        String email
) {
}
