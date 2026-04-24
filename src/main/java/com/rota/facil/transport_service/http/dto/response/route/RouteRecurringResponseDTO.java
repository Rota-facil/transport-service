package com.rota.facil.transport_service.http.dto.response.route;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;

import java.util.Set;
import java.util.UUID;

public record RouteRecurringResponseDTO(
        UUID id,
        Set<DaysOfWeek> daysOfWeek
) {
}
