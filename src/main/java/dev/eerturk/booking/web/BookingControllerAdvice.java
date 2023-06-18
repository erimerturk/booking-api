package dev.eerturk.booking.web;

import dev.eerturk.booking.BookingIsNotDeleteAbleException;
import dev.eerturk.booking.BookingNotFoundException;
import dev.eerturk.booking.ReservationAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookingControllerAdvice {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookingControllerAdvice.class);

  @ExceptionHandler(BookingNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String NotFoundHandler(BookingNotFoundException ex) {
    LOGGER.warn("The requested resource was not found", ex);
    return ex.getMessage();
  }

  @ExceptionHandler(ReservationAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  String alreadyExistsHandler(ReservationAlreadyExistsException ex) {
    LOGGER.warn("The requested dates was not available", ex);
    return ex.getMessage();
  }

  @ExceptionHandler(BookingIsNotDeleteAbleException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  String notAbleToDeleteHandler(BookingIsNotDeleteAbleException ex) {
    LOGGER.warn("The requested booking was not able to delete", ex);
    return ex.getMessage();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    var errors = new HashMap<String, String>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return errors;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    LOGGER.error("An internal server error occurred", e);
    return new ResponseEntity<>(
        "An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
