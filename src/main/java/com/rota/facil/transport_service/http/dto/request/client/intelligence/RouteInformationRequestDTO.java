package com.rota.facil.transport_service.http.dto.request.client.intelligence;

import com.rota.facil.transport_service.domain.enums.Shift;
import com.rota.facil.transport_service.domain.enums.DaysOfWeek;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public record RouteInformationRequestDTO(
        LocalTime expectedGoing,
        LocalTime expectedGoingFinish,
        LocalTime expectedReturn,
        LocalTime expectedReturnFinish,
        Shift shift,
        Set<DaysOfWeek> recurringDays,
        List<RouteInformationTripHistory> tripHistory
) {
}
