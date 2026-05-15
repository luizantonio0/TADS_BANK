package com.bantads.auth.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bantads.auth.exception.HttpException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleValidationErrors(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> handleHttpException(HttpException ex) {
        var status = HttpStatus.valueOf(ex.getStatusCode());
        String msg = status.is5xxServerError() ? "Algo deu errado. Tente novamente mais tarde." : ex.getMessage();
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }

}
