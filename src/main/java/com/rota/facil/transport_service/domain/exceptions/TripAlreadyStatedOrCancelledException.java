package com.rota.facil.transport_service.domain.exceptions;

public class TripAlreadyStatedOrCancelledException extends RuntimeException {
    public TripAlreadyStatedOrCancelledException(String message) {
        super(message);
    }

    public TripAlreadyStatedOrCancelledException() {
        super("Viagem já foi iniciada ou já foi cancelada");
    }
}
