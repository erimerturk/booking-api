package dev.eerturk.booking.domain;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface BookingDateRepository extends CrudRepository<BookingDate, Long> {
    boolean existsByPropertyIdAndBookingTypeAndDateBetween(Long propertyId, BookingType type, LocalDate startDate, LocalDate endDate);
    boolean existsByPropertyIdAndDateBetween(Long propertyId, LocalDate startDate, LocalDate endDate);
}
