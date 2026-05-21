package com.bantads.cliente.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bantads.cliente.exception.HttpException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Algo deu errado. Tente novamente mais tarde."));
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> handleHttpException(HttpException ex) {
        var status = HttpStatus.valueOf(ex.getStatusCode());
        String msg = status.is5xxServerError() ? "Algo deu errado. Tente novamente mais tarde." : ex.getMessage();
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }

}