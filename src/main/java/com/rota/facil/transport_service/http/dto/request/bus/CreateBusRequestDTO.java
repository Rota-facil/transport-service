package com.rota.facil.transport_service.http.dto.request.bus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBusRequestDTO(
        UUID driverId,

        @NotNull(message = "capacidade é obrigatória")
        Long capacity,

        @NotBlank(message = "placa é obrigatória")
        String plate
) {
}
