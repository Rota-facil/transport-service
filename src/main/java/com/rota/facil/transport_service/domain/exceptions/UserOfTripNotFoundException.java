package com.rota.facil.transport_service.domain.exceptions;

public class UserOfTripNotFoundException extends RuntimeException {
    public UserOfTripNotFoundException(String message) {
        super(message);
    }

    public UserOfTripNotFoundException() {
        super("Estudante não está registrado nessa viagem");
    }
}
