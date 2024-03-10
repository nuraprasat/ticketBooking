package com.train.ticket.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles exceptions of type TicketNotFoundException thrown by controller methods.
     * when a ticket cannot be found, encapsulating the error details in an APIError object.
     *
     * @param ex The caught TicketNotFoundException instance containing the error details.
     * @return A ResponseEntity object containing the APIError from the exception and
     *         the HTTP status code BAD_REQUEST (400), indicating that the client's request
     *         cannot be processed due to semantic errors.
     */
    @ExceptionHandler({TicketNotFoundException.class})
    protected ResponseEntity<Object> handleProductNotFoundException(
            TicketNotFoundException ex) {
        return new ResponseEntity<>(ex.getApiError(), BAD_REQUEST);
    }

    /**
     * Handles exceptions of type SeatNotAvailableException thrown by controller methods.
     * when a seat is not available, encapsulating the error details in an APIError object.
     *
     * @param ex The caught SeatNotAvailableException instance containing the error details.
     * @return A ResponseEntity object containing the APIError from the exception and
     *         the HTTP status code BAD_REQUEST (400), indicating that the client's request
     *         cannot be processed due to semantic errors.
     */
    @ExceptionHandler({SeatNotAvailableException.class})
    protected ResponseEntity<Object> handleProductNotFoundException(
            SeatNotAvailableException ex) {
        return new ResponseEntity<>(ex.getApiError(), BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
