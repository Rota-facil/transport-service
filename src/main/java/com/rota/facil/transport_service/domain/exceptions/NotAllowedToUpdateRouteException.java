package com.rota.facil.transport_service.domain.exceptions;

public class NotAllowedToUpdateRouteException extends RuntimeException {
    public NotAllowedToUpdateRouteException(String message) {
        super(message);
    }
    public NotAllowedToUpdateRouteException() {
        super();
    }
}
