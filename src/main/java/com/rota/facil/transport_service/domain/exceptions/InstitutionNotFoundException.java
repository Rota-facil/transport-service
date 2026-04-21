package com.rota.facil.transport_service.domain.exceptions;

public class InstitutionNotFoundException extends RuntimeException {
    public InstitutionNotFoundException(String message) {
        super(message);
    }

    public InstitutionNotFoundException() {
        super("Instituição não foi encontrada");
    }
}
