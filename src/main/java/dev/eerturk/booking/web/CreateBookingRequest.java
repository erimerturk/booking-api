package dev.eerturk.booking.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateBookingRequest(
    @NotNull(message = "The property id must be defined.")
        @Positive(message = "The property id must be greater than zero")
        Long propertyId,
    @NotNull(message = "The booking start date must be defined.") LocalDate startDate,
    @NotNull(message = "The booking end date must be defined.") LocalDate endDate,
    @Positive(message = "The guest id must be greater than zero") Long guestId) {

  public static CreateBookingRequest of(
      Long propertyId, LocalDate startDate, LocalDate endDate, Long guestId) {
    return new CreateBookingRequest(propertyId, startDate, endDate, guestId);
  }
}
