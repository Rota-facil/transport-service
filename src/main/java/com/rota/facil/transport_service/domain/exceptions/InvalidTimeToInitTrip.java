package com.rota.facil.transport_service.domain.exceptions;

import org.springframework.beans.factory.annotation.Value;

public class InvalidTimeToInitTrip extends RuntimeException {
    public InvalidTimeToInitTrip(String message) {
        super(message);
    }
}
