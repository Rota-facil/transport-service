package com.rota.facil.transport_service.http.dto.request.route;

import com.rota.facil.transport_service.domain.enums.Shift;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record CreateRouteRequestDTO(
    Shift shift,
    LocalTime going,
    LocalTime return_,
    LocalTime goingFinish,
    LocalTime returnFinish,
    Set<UUID> institutionsIds
) {
}
