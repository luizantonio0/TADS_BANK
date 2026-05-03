package com.bantads.gerente.exception;

public class AccountAlredyExists extends RuntimeException {
    public AccountAlredyExists(String message) {
        super(message);
    }
}
