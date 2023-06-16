package dev.eerturk.booking.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class BookingDateRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private BookingDateRepository bookingDateRepository;

    @Test
    void shouldVerifyExistsByPropertyIdAndBookingTypeAndDateBetween() {
        Booking booking = new Booking();
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setGuestId(333l);
        booking.setPropertyId(222l);
        booking.setStatus(Status.ACTIVE);
        booking.setBookingType(BookingType.RESERVATION);
        booking.initDates();


        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDate.now().plusDays(10));
        booking2.setEndDate(LocalDate.now().plusDays(20));
        booking2.setGuestId(333l);
        booking2.setPropertyId(4444l);
        booking2.setStatus(Status.ACTIVE);
        booking2.setBookingType(BookingType.RESERVATION);
        booking2.initDates();

        repository.saveAll(Arrays.asList(booking, booking2));

        assertThat(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                booking.getPropertyId(),
                BookingType.RESERVATION,
                booking.getStartDate(),
                booking.getStartDate().plusDays(100))
        ).isTrue();

        assertThat(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                booking.getPropertyId(),
                BookingType.BLOCK,
                booking.getStartDate(),
                booking.getStartDate().plusDays(100))
        ).isFalse();

        assertThat(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                booking.getPropertyId(),
                BookingType.RESERVATION,
                booking.getStartDate().plusDays(90),
                booking.getStartDate().plusDays(100))
        ).isFalse();

        assertThat(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                booking.getPropertyId(),
                BookingType.RESERVATION,
                booking.getStartDate().minusDays(100),
                booking.getStartDate().minusDays(90))
        ).isFalse();

        assertThat(bookingDateRepository.existsByPropertyIdAndBookingTypeAndDateBetween(
                booking.getPropertyId(),
                BookingType.RESERVATION,
                booking.getStartDate().minusDays(10),
                booking.getStartDate())
        ).isTrue();
    }

}