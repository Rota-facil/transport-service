package com.rota.facil.transport_service.http.dto.response.metric;

public record MetricResponse(
        Long activeRoutes,
        Long tripsToday,
        Long cancelledTrips,
        Long students,
        Long drivers,
        Long bus
) {
}
