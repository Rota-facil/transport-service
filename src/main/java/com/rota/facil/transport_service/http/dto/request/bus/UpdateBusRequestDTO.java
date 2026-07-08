package com.rota.facil.transport_service.http.dto.request.bus;

import com.rota.facil.transport_service.domain.enums.BusStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record UpdateBusRequestDTO(
        UUID driverId,

        @Positive(message = "capacidade deve ser maior que zero")
        Long capacity,

        @NotBlank(message = "placa é obrigatória")
        String plate,

        BusStatus status
) {
}
