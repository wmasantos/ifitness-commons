package br.com.ifitness.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IFitnessException extends RuntimeException {
    private int code;
    private HttpStatus httpStatus;

    public IFitnessException(String message, int code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
