package com.rota.facil.transport_service.http.dto.response.tripUser;

import java.util.UUID;

public record TripUserResponseDTO(
        UUID id,
        TripUserUserResponseDTO user,
        TripUserInstitutionResponseDTO institution,
        TripUserBoardPointResponseDTO boardPoint,
        Boolean present,
        Boolean going,
        Boolean return_
) {
}
