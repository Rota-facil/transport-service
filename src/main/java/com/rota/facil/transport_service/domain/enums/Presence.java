package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Presence {
    CHECKIN("Check-in"),
    PENDING("Pendente"),
    ABSENT("Ausente");

    private final String description;
}
