package com.bantads.conta.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}
