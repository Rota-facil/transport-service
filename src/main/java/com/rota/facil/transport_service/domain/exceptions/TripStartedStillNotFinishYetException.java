package com.rota.facil.transport_service.domain.exceptions;

public class TripStartedStillNotFinishYetException extends RuntimeException {
    public TripStartedStillNotFinishYetException(String message) {
        super(message);
    }

    public TripStartedStillNotFinishYetException() {
        super("A ida da viagem ainda não foi finalizada");
    }
}
