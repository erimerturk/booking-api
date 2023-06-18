package dev.eerturk.booking;

public class BookingNotFoundException extends RuntimeException {
  public BookingNotFoundException(Long id) {
    super("A block with ID " + id + " not found.");
  }
}
