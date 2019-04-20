package br.com.ifitness.commons.controller;

import br.com.ifitness.commons.dto.ResponseSimple;
import br.com.ifitness.commons.exception.IFitnessException;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(0)
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {
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
}
