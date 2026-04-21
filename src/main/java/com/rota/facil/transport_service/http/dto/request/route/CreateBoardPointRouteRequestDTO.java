package com.rota.facil.transport_service.http.dto.request.route;

import java.time.LocalTime;
import java.util.UUID;

public record CreateBoardPointRouteRequestDTO(
        UUID boardPointId,
        LocalTime boardTimeGoing,
        LocalTime boardTimeFinish
) {
}
