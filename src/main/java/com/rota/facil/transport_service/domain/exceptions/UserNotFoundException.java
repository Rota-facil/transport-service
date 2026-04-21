package com.rota.facil.transport_service.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuário não foi encontrado");
    }
}
