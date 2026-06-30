package com.rota.facil.transport_service.messaging.dto.send.trip;

import java.util.UUID;

public record StudentInfoEventSend(
        UUID id,
        String name,
        String email
) { }
