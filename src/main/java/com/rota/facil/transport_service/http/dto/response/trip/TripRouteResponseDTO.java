package com.rota.facil.transport_service.http.dto.response.trip;

import com.rota.facil.transport_service.domain.enums.Shift;

import java.time.LocalTime;
import java.util.UUID;

public record TripRouteResponseDTO(
        UUID id,
        Shift shift,
        LocalTime going,
        LocalTime return_,
        LocalTime goingFinish,
        LocalTime returnFinish
) {
}
