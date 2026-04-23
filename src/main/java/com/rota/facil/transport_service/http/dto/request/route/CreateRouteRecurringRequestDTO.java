package com.rota.facil.transport_service.http.dto.request.route;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateRouteRecurringRequestDTO(
        @NotNull(message = "ônibus é obrigatório")
        UUID busId,

        @NotNull(message = "selecione pelo menos um dia da semana que ônibus fara a rota")
        List<DaysOfWeek> daysOfWeeks
) {
}
