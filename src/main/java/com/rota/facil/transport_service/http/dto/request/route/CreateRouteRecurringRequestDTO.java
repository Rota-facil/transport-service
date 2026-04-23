package com.rota.facil.transport_service.http.dto.request.route;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateRouteRecurringRequestDTO(
        UUID busId,
        LocalDate startDate,
        LocalDate finishDate,
        List<DaysOfWeek> daysOfWeeks
) {
}
