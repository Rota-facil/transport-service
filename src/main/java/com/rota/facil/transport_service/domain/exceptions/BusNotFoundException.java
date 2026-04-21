package com.rota.facil.transport_service.domain.exceptions;

public class BusNotFoundException extends RuntimeException {
    public BusNotFoundException(String message) {
        super(message);
    }

    public BusNotFoundException() {
        super("Ônibus não foi encontrado");
    }
}
