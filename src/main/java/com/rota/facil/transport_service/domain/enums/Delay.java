package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Delay {
    LATE("Atrasado"),
    PUNCTUAL("Pontual"),
    EARLY("Adiantado");

    private final String title;
}
