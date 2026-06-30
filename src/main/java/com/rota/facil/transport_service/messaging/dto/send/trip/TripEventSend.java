package com.rota.facil.transport_service.messaging.dto.send.trip;

import com.rota.facil.transport_service.domain.enums.ActionType;
import com.rota.facil.transport_service.domain.enums.ResourceName;
import com.rota.facil.transport_service.domain.enums.Role;

import java.util.List;
import java.util.UUID;

public record TripEventSend(
        UUID userId,
        Role role,
        String actionTitle,
        ActionType actionType,
        ResourceName resourceName,
        UUID resourceId,

        UUID prefectureId,
        UUID driverId,
        String driverEmail,
        UUID tripId,
        String routeName,
        String reasonOfCancellation,
        String latitude,
        String longitude,
        List<StudentInfoEventSend> studentInfo
) {
}
