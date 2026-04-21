package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {
    CREATE(" criou "),
    UPDATE(" atualizou dados de "),
    DELETE( " deletou ");

    private final String title;
}
