package com.rota.facil.transport_service.http.dto.request.trip;

import jakarta.validation.constraints.NotBlank;

public record CancelTripRequestDTO(
        @NotBlank(message = "razão de cancelamento é obrigatória")
        String reasonOfCancellation
) {
}
