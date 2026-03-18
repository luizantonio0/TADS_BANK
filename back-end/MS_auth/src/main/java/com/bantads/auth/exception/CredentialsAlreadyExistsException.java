package com.bantads.auth.exception;

public class CredentialsAlreadyExistsException extends RuntimeException {
    public CredentialsAlreadyExistsException() {
        super("Credenciais para esse usuário ja estão definidas.");
    }
}
