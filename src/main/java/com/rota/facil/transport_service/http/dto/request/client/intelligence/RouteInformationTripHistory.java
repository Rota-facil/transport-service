package com.rota.facil.transport_service.http.dto.request.client.intelligence;

import java.util.List;

public record RouteInformationTripHistory(
        List<String> expectedInstitutionsPassed,
        List<String> actualInstitutionsPassed,
        List<RouteInformationTripStatus> status
) {
}
