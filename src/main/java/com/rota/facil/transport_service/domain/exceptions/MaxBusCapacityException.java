package com.rota.facil.transport_service.domain.exceptions;

public class MaxBusCapacityException extends RuntimeException {
    public MaxBusCapacityException(String message) {
        super(message);
    }
}
