package dev.eerturk.booking.dao;

import dev.eerturk.booking.model.BookingDate;
import dev.eerturk.booking.model.BookingType;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDateRepository extends CrudRepository<BookingDate, Long> {
  boolean existsByPropertyIdAndBookingTypeAndDateBetween(
      Long propertyId, BookingType type, LocalDate startDate, LocalDate endDate);

  boolean existsByPropertyIdAndDateBetween(Long propertyId, LocalDate startDate, LocalDate endDate);
}
