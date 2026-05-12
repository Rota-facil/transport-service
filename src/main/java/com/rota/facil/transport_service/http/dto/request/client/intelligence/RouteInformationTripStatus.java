package com.rota.facil.transport_service.http.dto.request.client.intelligence;

import com.rota.facil.transport_service.domain.enums.Delay;
import com.rota.facil.transport_service.domain.enums.Progress;

import java.time.LocalDateTime;

public record RouteInformationTripStatus(
        Progress progress,
        Delay delay,
        LocalDateTime eventOccurred
) {
}
