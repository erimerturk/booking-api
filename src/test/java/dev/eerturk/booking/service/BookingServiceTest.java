package dev.eerturk.booking.service;

import dev.eerturk.booking.BookingIsNotDeleteAbleException;
import dev.eerturk.booking.BookingNotFoundException;
import dev.eerturk.booking.ReservationAlreadyExistsException;
import dev.eerturk.booking.dao.BookingDateRepository;
import dev.eerturk.booking.dao.BookingRepository;
import dev.eerturk.booking.model.Booking;
import dev.eerturk.booking.model.BookingType;
import dev.eerturk.booking.model.Status;
import dev.eerturk.booking.web.CreateBookingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");

    @Mock
    private BookingRepository repository;

    @Mock
    private BookingDateRepository bookingDateRepository;

    @InjectMocks
    private BookingService service;

    @Test
    void shouldThrowReservationAlreadyExistsWhenRequestIsBlockAndDateRangeHasReservation() {
        var toCreate = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), null);
        when(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                toCreate.propertyId(),
                BookingType.RESERVATION,
                toCreate.startDate(),
                toCreate.endDate()
                )).thenReturn(true);
        assertThatThrownBy(() -> service.create(toCreate))
                .isInstanceOf(ReservationAlreadyExistsException.class)
                .hasMessage(String.format(
                        "A reservation with property ID %d between %s and %s is not available.",
                        toCreate.propertyId(),
                        DATE_FORMATTER.format(toCreate.startDate()),
                        DATE_FORMATTER.format(toCreate.endDate()))
                );
        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowReservationAlreadyExistsWhenRequestIsReservationAndDateRangeHasReservation() {
        var toCreate = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), 333l);
        when(bookingDateRepository.existsByPropertyIdAndDateBetween(
                toCreate.propertyId(),
                toCreate.startDate(),
                toCreate.endDate()
        )).thenReturn(true);
        assertThatThrownBy(() -> service.create(toCreate))
                .isInstanceOf(ReservationAlreadyExistsException.class)
                .hasMessage(String.format(
                        "A reservation with property ID %d between %s and %s is not available.",
                        toCreate.propertyId(),
                        DATE_FORMATTER.format(toCreate.startDate()),
                        DATE_FORMATTER.format(toCreate.endDate()))
                );
        verifyNoInteractions(repository);
    }

    @Test
    void shouldCreateReservationWhenDateRangeIsAvailable() {
        var toCreate = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), null);
        when(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                toCreate.propertyId(),
                BookingType.RESERVATION,
                toCreate.startDate(),
                toCreate.endDate()
        )).thenReturn(false);

        service.create(toCreate);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue())
                .satisfies(toDB ->{
                    assertThat(toDB.getDates()).hasSize(1);
                    assertThat(toDB.isBlock()).isTrue();
                });

    }

    @Test
    void shouldThrowBookingIsNotDeleteAbleExceptionWhenBookingIsNotBlock() {
        Booking booking = new Booking();
        booking.setId(999L);
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setPropertyId(222l);
        booking.setStatus(Status.ACTIVE);
        booking.setBookingType(BookingType.RESERVATION);
        booking.initDates();


        when(repository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> service.delete(booking.getId()))
                .isInstanceOf(BookingIsNotDeleteAbleException.class)
                .hasMessage("A booking with ID " + booking.getId() + " not delete able.");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowBookingNotFoundExceptionWhenBookingStatusIsNotActiveOnCancel() {
        Booking booking = new Booking();
        booking.setId(999L);
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setPropertyId(222l);
        booking.setStatus(Status.CANCEL);
        booking.setBookingType(BookingType.RESERVATION);
        booking.initDates();

        when(repository.findByIdAndStatus(booking.getId(), Status.ACTIVE))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.cancel(booking.getId()))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("A block with ID " + booking.getId() + " not found.");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteDatesWhenBookingIsCancelled() {
        Booking booking = new Booking();
        booking.setId(999L);
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setPropertyId(222l);
        booking.setStatus(Status.ACTIVE);
        booking.setBookingType(BookingType.RESERVATION);
        booking.initDates();

        when(repository.findByIdAndStatus(booking.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(booking));

        service.cancel(booking.getId());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue())
                .satisfies(toDB ->{
                    assertThat(toDB.getDates()).hasSize(0);
                    assertThat(toDB.getStatus()).isEqualTo(Status.CANCEL);
                });

    }

    @Test
    void shouldThrowBookingNotFoundExceptionWhenBookingStatusIsNotCancelOnRebook() {
        Booking booking = new Booking();
        booking.setId(999L);
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setPropertyId(222l);
        booking.setStatus(Status.CANCEL);
        booking.setBookingType(BookingType.RESERVATION);
        booking.initDates();

        when(repository.findByIdAndStatus(booking.getId(), Status.CANCEL))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.rebook(booking.getId()))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("A block with ID " + booking.getId() + " not found.");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowReservationAlreadyExistsWhenRebookingDateNotAvailableOnRebook() {
        Booking booking = new Booking();
        booking.setId(999L);
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setPropertyId(222l);
        booking.setGuestId(4234234l);
        booking.setStatus(Status.CANCEL);
        booking.setBookingType(BookingType.RESERVATION);
        booking.initDates();

        when(repository.findByIdAndStatus(booking.getId(), Status.CANCEL))
                .thenReturn(Optional.of(booking));

        when(bookingDateRepository.existsByPropertyIdAndDateBetween(
                booking.getPropertyId(),
                booking.getStartDate(),
                booking.getEndDate()
        )).thenReturn(true);

        assertThatThrownBy(() -> service.rebook(booking.getId()))
                .isInstanceOf(ReservationAlreadyExistsException.class)
                .hasMessage(String.format(
                        "A reservation with property ID %d between %s and %s is not available.",
                        booking.getPropertyId(),
                        DATE_FORMATTER.format(booking.getStartDate()),
                        DATE_FORMATTER.format(booking.getEndDate()))
                );
        verifyNoMoreInteractions(repository);
    }


}