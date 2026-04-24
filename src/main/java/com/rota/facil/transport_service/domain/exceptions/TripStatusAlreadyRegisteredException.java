package com.rota.facil.transport_service.domain.exceptions;

public class TripStatusAlreadyRegisteredException extends RuntimeException {
    public TripStatusAlreadyRegisteredException(String message) {
        super(message);
    }

    public TripStatusAlreadyRegisteredException() {
        super("Status já existente na viagem");
    }
}
