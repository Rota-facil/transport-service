package com.rota.facil.transport_service.domain.exceptions;

public class BusWithoutDriverException extends RuntimeException {
    public BusWithoutDriverException(String message) {
        super(message);
    }

    public BusWithoutDriverException() {
        super("Ônibus sem motorista vinculado");
    }
}
