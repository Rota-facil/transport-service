package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportAuditAction {
    ROUTE_CREATED("CREATE", "%s criou a rota %s"),
    ROUTE_UPDATED("UPDATE", "%s atualizou a rota %s"),
    ROUTE_DELETED("DELETE", "%s deletou a rota %s"),
    BUS_CREATED("CREATE", "%s cadastrou o onibus %s"),
    BUS_UPDATED("UPDATE", "%s atualizou o onibus %s"),
    BUS_DELETED("DELETE", "%s deletou o onibus %s"),
    USER_FEEDBACK("CREATE", "%s enviou feedback para %s"),
    TRIP_RUNNING("UPDATE", "%s iniciou a viagem da rota %s"),
    TRIP_CANCELLED("UPDATE", "%s cancelou a viagem da rota %s"),
    TRIP_DELETED("DELETE", "%s deletou a viagem da rota %s");

    private final String actionType;
    private final String titleTemplate;

    public String title(String actorEmail, String resourceName) {
        return this.titleTemplate.formatted(actorEmail, resourceName);
    }
}
