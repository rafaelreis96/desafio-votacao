package dev.rafaelreis.desafiovotacao.exception;

import java.io.Serial;

public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1;

    public BusinessException(String message) {
        super(message);
    }
}
