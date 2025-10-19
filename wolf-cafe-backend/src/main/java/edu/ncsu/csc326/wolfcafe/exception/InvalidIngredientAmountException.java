package edu.ncsu.csc326.wolfcafe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception if an ingredient amount is invalid
 */
@ResponseStatus ( value = HttpStatus.BAD_REQUEST )
public class InvalidIngredientAmountException extends RuntimeException {

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs the exception with the given message.
     *
     * @param message
     *            exception message
     */
    public InvalidIngredientAmountException ( final String message ) {
        super( message );
    }
}
