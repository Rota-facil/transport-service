package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Progress {
    NOT_STARTED("Não iniciada"),
    CANCELLED("Cancelada"),
    STARTED("Iniciou"),
    STARTED_FINISHED("Início finalizado"),
    RETURN_STARTED("Retorno iniciado"),
    RETURN_FINISHED("Retorno finalizado");

    private final String title;
}