package com.rota.facil.transport_service.domain.exceptions;

public class DriverAlreadyHasBusException extends RuntimeException {
    public DriverAlreadyHasBusException(String message) {
        super(message);
    }

    public DriverAlreadyHasBusException() {
        super("Motorista já possui ônibus vinculado");
    }
}
