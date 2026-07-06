package com.rota.facil.transport_service.domain.exceptions;

public class DriverIsOnRouteException extends RuntimeException {
    public DriverIsOnRouteException(String message) {
        super(message);
    }
}
