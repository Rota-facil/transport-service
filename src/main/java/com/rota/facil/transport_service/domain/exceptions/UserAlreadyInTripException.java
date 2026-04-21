package com.rota.facil.transport_service.domain.exceptions;

public class UserAlreadyInTripException extends RuntimeException {
    public UserAlreadyInTripException(String message) {
        super(message);
    }

    public UserAlreadyInTripException() {
        super("Você já está inscrito nessa viagaem");
    }

}
