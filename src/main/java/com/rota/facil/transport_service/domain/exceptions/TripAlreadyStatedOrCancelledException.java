package com.rota.facil.transport_service.domain.exceptions;

public class TripAlreadyStatedExcpetion extends RuntimeException {
    public TripAlreadyStatedExcpetion(String message) {
        super(message);
    }

    public TripAlreadyStatedExcpetion() {
        super("Viagem já foi iniciada");
    }
}
