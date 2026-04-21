package com.rota.facil.transport_service.domain.exceptions;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String message) {
        super(message);
    }

    public TripNotFoundException() {
        super("Viagem não foi encontrada");
    }

}
