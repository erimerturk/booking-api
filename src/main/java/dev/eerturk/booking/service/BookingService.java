package dev.eerturk.booking.service;

import dev.eerturk.booking.BookingIsNotDeleteAbleException;
import dev.eerturk.booking.BookingNotFoundException;
import dev.eerturk.booking.ReservationAlreadyExistsException;
import dev.eerturk.booking.dao.BookingDateRepository;
import dev.eerturk.booking.dao.BookingRepository;
import dev.eerturk.booking.dto.BookingDetailResponse;
import dev.eerturk.booking.model.Booking;
import dev.eerturk.booking.model.BookingType;
import dev.eerturk.booking.model.Status;
import dev.eerturk.booking.web.CreateBookingRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final BookingDateRepository bookingDateRepository;

    public BookingService(BookingRepository repository, BookingDateRepository bookingDateRepository) {
        this.repository = repository;
        this.bookingDateRepository = bookingDateRepository;
    }

    @Transactional(readOnly = true)
    public List<BookingDetailResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(each -> new BookingDetailResponse(
                        each.getId(),
                        each.getPropertyId(),
                        each.getStartDate(),
                        each.getEndDate(),
                        each.getGuestId(),
                        each.getStatus()
                )).collect(Collectors.toList());
    }

    @Transactional
    public BookingDetailResponse create(CreateBookingRequest command) {
        Booking booking = new Booking();
        booking.setStatus(Status.ACTIVE)
                .setStartDate(command.startDate())
                .setEndDate(command.endDate())
                .setGuestId(command.guestId())
                .setPropertyId(command.propertyId())
                .setBookingType(command.guestId() != null ? BookingType.RESERVATION : BookingType.BLOCK);
        booking.initDates();
        validateBookingDates(booking);
        repository.save(booking);
        return new BookingDetailResponse(
                booking.getId(),
                booking.getPropertyId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getGuestId(),
                booking.getStatus()
        );
    }

    private void validateBookingDates(Booking booking) {
        if (booking.isBlock()) {
            boolean existReservation = bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                    booking.getPropertyId(),
                    BookingType.RESERVATION,
                    booking.getStartDate(),
                    booking.getEndDate());

            if (existReservation) {
                throw new ReservationAlreadyExistsException(booking.getStartDate(), booking.getEndDate(), booking.getPropertyId());
            }
        } else {
            boolean existReservation = bookingDateRepository.existsByPropertyIdAndDateBetween(
                    booking.getPropertyId(),
                    booking.getStartDate(),
                    booking.getEndDate());

            if (existReservation) {
                throw new ReservationAlreadyExistsException(booking.getStartDate(), booking.getEndDate(), booking.getPropertyId());
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        Booking booking = getBookingBy(id);
        if (!booking.isBlock()) {
            throw new BookingIsNotDeleteAbleException(id);
        }
        repository.delete(booking);
    }

    private Booking getBookingBy(Long id, Status status) {
        return repository.findByIdAndStatus(id, status)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    private Booking getBookingBy(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Transactional
    public void cancel(Long id) {
        Booking booking = getBookingBy(id, Status.ACTIVE);
        booking.deleteDates();
        booking.setStatus(Status.CANCEL);
        repository.save(booking);
    }

    @Transactional
    public void rebook(Long id) {
        Booking booking = getBookingBy(id, Status.CANCEL);
        validateBookingDates(booking);
        booking.setStatus(Status.ACTIVE);
        booking.initDates();
        repository.save(booking);
    }
}
