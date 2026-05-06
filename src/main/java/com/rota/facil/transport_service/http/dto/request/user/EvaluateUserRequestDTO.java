package com.rota.facil.transport_service.http.dto.request.user;

import jakarta.validation.constraints.*;

public record EvaluateUserRequestDTO(
        @NotBlank(message = "feedback é obrigatório")
        String feedback,

        @NotNull(message = "nota é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor mínimo permitido é 0.0")
        @DecimalMax(value = "5.0", message = "O valor máximo permitido é 5.0")
        Double note
) {
}
