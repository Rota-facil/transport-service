package com.rota.facil.transport_service.http.dto.request.route;

import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;

import java.util.List;
import java.util.UUID;

public record RouteHeatMapRequestDTO(
        CurrentUser currentUser,
        UUID routeId,
        List<PointRequestDTO> points
) {
}
