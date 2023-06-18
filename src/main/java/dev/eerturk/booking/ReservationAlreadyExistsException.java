package dev.eerturk.booking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationAlreadyExistsException extends RuntimeException {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public ReservationAlreadyExistsException(LocalDate startDate, LocalDate endDate, Long propertyId) {
        super(String.format("A reservation with property ID %d between %s and %s is not available.", propertyId, DATE_FORMATTER.format(startDate), DATE_FORMATTER.format(endDate)));
    }
}
