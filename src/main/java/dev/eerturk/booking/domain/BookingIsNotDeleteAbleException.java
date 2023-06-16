package dev.eerturk.booking.domain;

public class BookingIsNotDeleteAbleException extends RuntimeException {
    public BookingIsNotDeleteAbleException(Long id) {
        super("A booking with ID " + id + " not delete able.");
    }
}
