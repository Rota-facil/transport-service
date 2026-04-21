package com.rota.facil.transport_service.http.dto.request.trip;

import java.util.UUID;

public record JoinUserInTrip(
        UUID boardPointId,
        UUID institutionId,
        Boolean going,
        Boolean return_
) {
}
