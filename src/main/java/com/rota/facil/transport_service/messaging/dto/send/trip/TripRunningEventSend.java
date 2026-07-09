package com.rota.facil.transport_service.messaging.dto.send.trip;

import com.rota.facil.transport_service.persistence.dto.StudentPersistenceDTO;

import java.util.List;
import java.util.UUID;

public record TripRunningEventSend(
        UUID userId,
        String role,
        String userEmail,
        String actionTitle,
        String actionType,
        String resourceName,
        UUID resourceId,
        UUID prefectureId,
        UUID driverId,
        String driverEmail,
        UUID tripId,
        String routeName,
        String latitude,
        String longitude,
        List<StudentPersistenceDTO> studentInfo
) {
}
