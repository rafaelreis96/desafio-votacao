package dev.rafaelreis.desafiovotacao.exception;

import java.io.Serial;

public class IntegrationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public IntegrationException(String message) {
        super(message);
    }
}
