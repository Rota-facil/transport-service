package com.rota.facil.transport_service.http.dto.response.route;

import com.rota.facil.transport_service.domain.enums.Shift;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RouteResponseDTO(
        UUID id,
        Shift shift,
        LocalTime going,
        LocalTime return_,
        LocalTime goingFinish,
        LocalTime returnFinish,
        LocalDateTime createdAt,
        Set<RouteInstitutionResponseDTO> institutions,
        List<RouteBoardPointResponseDTO> boardPoints
) {
}
