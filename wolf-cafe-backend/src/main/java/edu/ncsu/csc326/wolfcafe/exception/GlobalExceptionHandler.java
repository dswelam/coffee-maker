package edu.ncsu.csc326.wolfcafe.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles global errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * handler for exception
     *
     * @param ex
     *            the exception to handle
     * @return the error message
     */
    @ExceptionHandler ( IllegalStateException.class )
    @ResponseStatus ( HttpStatus.BAD_REQUEST )
    public Map<String, String> handleIllegalState ( final IllegalStateException ex ) {
        return Map.of( "error", ex.getMessage() );
    }
}
