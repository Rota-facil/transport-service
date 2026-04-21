package com.rota.facil.transport_service.domain.exceptions;

public class BoardPointNotFoundException extends RuntimeException {
    public BoardPointNotFoundException(String message) {
        super(message);
    }

    public BoardPointNotFoundException() {
        super("Ponto de embarque não foi encontrado");
    }
}
