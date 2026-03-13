package com.bantads.cliente.exceptions;

public class AccountAlredyExists extends RuntimeException {
    public AccountAlredyExists(String message) {
        super(message);
    }
}
