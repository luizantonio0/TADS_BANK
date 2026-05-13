package com.bantads.gerente.config;

import com.bantads.gerente.dto.ErroDTO;
import com.bantads.gerente.exception.AccountAlreadyExistsException;
import com.bantads.gerente.exception.BadRequestException;
import com.bantads.gerente.exception.NotFoundExecption;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundExecption.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErroDTO> handleNotFound(NotFoundExecption ex)
    {
        ErroDTO erro = new ErroDTO(
                "Recurso não encontrado:" + ex.getMessage(),
                Arrays.toString(ex.getStackTrace()),
                404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErroDTO> handleGeneric(Exception ex)
    {
        ErroDTO erro = new ErroDTO(
                "Erro interno no servidor:" + ex.getMessage(),
                Arrays.toString(ex.getStackTrace()),
                500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErroDTO> handleGeneric(AccountAlreadyExistsException ex)
    {
        ErroDTO erro = new ErroDTO(ex.getMessage(),
                Arrays.toString(ex.getStackTrace()),
                409);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErroDTO> handleNotFound(BadRequestException ex)
    {
        ErroDTO erro = new ErroDTO(
                "Dados Inválidos:" + ex.getMessage(),
                Arrays.toString(ex.getStackTrace()),
                400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}