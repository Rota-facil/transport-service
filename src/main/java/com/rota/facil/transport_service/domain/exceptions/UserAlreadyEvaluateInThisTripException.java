package com.rota.facil.transport_service.domain.exceptions;

public class UserAlreadyEvaluateInThisTripException extends RuntimeException {
    public UserAlreadyEvaluateInThisTripException(String message) {
        super(message);
    }

    public UserAlreadyEvaluateInThisTripException() {
        super("Usuário já foi avaliado na viagem atual");
    }
}
