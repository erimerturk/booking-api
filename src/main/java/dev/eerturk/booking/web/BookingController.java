package dev.eerturk.booking.web;

import dev.eerturk.booking.dto.BookingDetailResponse;
import dev.eerturk.booking.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bookings")
public class BookingController {
  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping
  public List<BookingDetailResponse> get() {
    return bookingService.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookingDetailResponse post(@Valid @RequestBody CreateBookingRequest createBookingRequest) {
    return bookingService.create(createBookingRequest);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    bookingService.delete(id);
  }

  @PutMapping("{id}/cancel")
  public void cancel(@PathVariable Long id) {
    bookingService.cancel(id);
  }

  @PutMapping("{id}/rebook")
  public void rebook(@PathVariable Long id) {
    bookingService.rebook(id);
  }
}
