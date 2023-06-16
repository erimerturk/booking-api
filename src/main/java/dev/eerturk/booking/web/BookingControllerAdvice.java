package dev.eerturk.booking.web;

import dev.eerturk.booking.domain.BookingIsNotDeleteAbleException;
import dev.eerturk.booking.domain.ReservationAlreadyExistsException;
import dev.eerturk.booking.domain.BookingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BookingControllerAdvice {

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String NotFoundHandler(BookingNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ReservationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String alreadyExistsHandler(ReservationAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BookingIsNotDeleteAbleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String notAbleToDeleteHandler(BookingIsNotDeleteAbleException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
