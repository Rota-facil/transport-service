package com.rota.facil.transport_service.domain.exceptions;

public class TripUserNotFoundException extends RuntimeException {
    public TripUserNotFoundException(String message) {
        super(message);
    }

    public TripUserNotFoundException() {
        super();
    }
}
