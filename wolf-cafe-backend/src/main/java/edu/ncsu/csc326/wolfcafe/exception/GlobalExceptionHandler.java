package edu.ncsu.csc326.wolfcafe.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles global errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles global API exceptions
     *
     * @param exception
     *            a WolfCafeAPI exception
     * @param webRequest
     *            the request that caused the exception
     * @return a ResponseEntity encapsulating the exception information for
     *         presentation to the front end
     */
    @ExceptionHandler ( WolfCafeAPIException.class )
    public ResponseEntity<ErrorDetails> handleAPIException ( final WolfCafeAPIException exception,
            final WebRequest webRequest ) {
        final ErrorDetails errorDetails = new ErrorDetails( LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription( false ) );

        return new ResponseEntity<>( errorDetails, HttpStatus.BAD_REQUEST );
    }

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
