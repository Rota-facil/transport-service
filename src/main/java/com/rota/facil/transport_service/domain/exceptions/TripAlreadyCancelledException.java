package com.rota.facil.transport_service.domain.exceptions;

public class TripAlreadyCancelledException extends RuntimeException {
    public TripAlreadyCancelledException(String message) {
        super(message);
    }

    public TripAlreadyCancelledException() {
        super("A viagem já foi cancelada anteriormente");
    }
}
