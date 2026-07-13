package com.rota.facil.transport_service.domain.exceptions;

public class BusNotAssociatedWithRouteException extends RuntimeException {
    public BusNotAssociatedWithRouteException() {
        super("O ônibus selecionado não está associado à rota");
    }
}
