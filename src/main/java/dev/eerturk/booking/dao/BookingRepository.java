package dev.eerturk.booking.dao;


import dev.eerturk.booking.model.Booking;
import dev.eerturk.booking.model.Status;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, Long> {
    Optional<Booking> findByIdAndStatus(Long id, Status status);
    Optional<Booking> findById(Long id);
}
