package com.rota.facil.transport_service.http.dto.response.tripUser;

import java.util.UUID;

public record SimpleTripUserResponseDTO(
        UUID id,
        TripUserUserResponseDTO user,
        String institutionName,
        String boardPointName,
        Boolean present,
        Boolean going,
        Boolean return_
) {
}
