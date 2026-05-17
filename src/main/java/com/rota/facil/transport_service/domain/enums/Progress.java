package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Progress {
    NOT_STARTED("Viagem não iniciada"),
    CANCELLED("Viagem cancelada"),
    STARTED("Ida iniciada"),
    STARTED_FINISHED("Ida finalizada"),
    RETURN_STARTED("Retorno iniciado"),
    RETURN_FINISHED("Retorno finalizado"),
    INSTITUTION_ARRIVAL("Chegada na instituição "),
    BOARD_POINT_ARRIVAL("Chegada no ponto de embarque ");

    private final String title;
}