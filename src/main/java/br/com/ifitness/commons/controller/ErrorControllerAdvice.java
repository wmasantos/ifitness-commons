package br.com.ifitness.commons.controller;

import br.com.ifitness.commons.dto.ResponseSimple;
import br.com.ifitness.commons.dto.ResponseValidation;
import br.com.ifitness.commons.dto.ResponseWithContentArray;
import br.com.ifitness.commons.exception.IFitnessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class ErrorControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseSimple> generalException(Exception e) {
        return new ResponseEntity<>(
            ResponseSimple
              .builder()
              .code(1)
              .httpCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
              .build(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IFitnessException.class)
    public ResponseEntity<ResponseSimple> customException(IFitnessException e) {
        return new ResponseEntity<>(
            ResponseSimple
                .builder()
                .code(e.getCode())
                .httpCode(e.getHttpStatus().value())
                .message(e.getMessage())
                .build(),
            e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWithContentArray<ResponseValidation>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        final List<ResponseValidation> responseValidations = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String value = ((FieldError) error).getRejectedValue().toString();
            String errorMessage = error.getDefaultMessage();

            responseValidations.add(ResponseValidation
                    .builder()
                    .field(fieldName)
                    .value(value)
                    .errorMessage(errorMessage)
                    .build());
        });

        return ResponseEntity
                .badRequest()
                .body(ResponseWithContentArray.<ResponseValidation>builder()
                    .code(0)
                    .httpCode(HttpStatus.BAD_REQUEST.value())
                    .message("Erros retornados")
                    .content(responseValidations)
                    .build());
    }
}
