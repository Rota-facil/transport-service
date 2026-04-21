package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Shift {
    MORNING("Manhã"),
    AFTERNOON("Tarde"),
    NIGHT("Noite");

    private final String title;
}
