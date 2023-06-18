package dev.eerturk.booking.dao;

import dev.eerturk.booking.model.Booking;
import dev.eerturk.booking.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class BookingRepositoryTests {

    @Autowired
    private BookingRepository repository;

    @Test
    void findAll() {
        Booking booking = new Booking();
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(20));
        booking.setGuestId(333l);
        booking.setPropertyId(222l);
        booking.setStatus(Status.ACTIVE);
        booking.initDates();


        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDate.now().plusDays(10));
        booking2.setEndDate(LocalDate.now().plusDays(20));
        booking2.setGuestId(333l);
        booking2.setPropertyId(4444l);
        booking2.setStatus(Status.ACTIVE);
        booking2.initDates();

        repository.saveAll(Arrays.asList(booking, booking2));

        List<Booking> all = repository.findAll();

        assertThat(all.stream()
                .filter(each -> each.getPropertyId().equals(booking.getPropertyId()) || each.getPropertyId().equals(booking2.getPropertyId()))
                .collect(Collectors.toList())).hasSize(2);
    }

}
