package dev.eerturk.booking;

import dev.eerturk.booking.dto.BookingDetailResponse;
import dev.eerturk.booking.model.Status;
import dev.eerturk.booking.web.CreateBookingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class BookingApiApplicationTests {

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void shouldCreateAndListBookings() {
        var propertyId = Instant.now().toEpochMilli();
        var toCreate = CreateBookingRequest.of(
                propertyId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                5l
        );
        BookingDetailResponse expected = webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(toCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                            assertThat(booking.propertyId()).isEqualTo(toCreate.propertyId());
                        }
                )
                .returnResult().getResponseBody();

        webTestClient
                .get()
                .uri("/bookings")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(BookingDetailResponse.class)
                .contains(expected)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void shouldThrowReservationAlreadyExistsWhenRequestIsReservationAndDateRangeHasReservation() {
        var toCreate = CreateBookingRequest.of(
                Instant.now().toEpochMilli(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                5l
        );
        webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(toCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                            assertThat(booking.propertyId()).isEqualTo(toCreate.propertyId());
                        }
                )
                .returnResult().getResponseBody();

        webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(CreateBookingRequest.of(
                        toCreate.propertyId(),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(15),
                        9l
                ))
                .exchange()
                .expectStatus().is4xxClientError();

    }

    @Test
    void shouldAllowOverlapBlockCreation() {
        var toCreate = CreateBookingRequest.of(
                Instant.now().toEpochMilli(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                null
        );
        BookingDetailResponse booking1 = webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(toCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.propertyId()).isEqualTo(toCreate.propertyId());
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                        }
                )
                .returnResult().getResponseBody();

        BookingDetailResponse booking2 = webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(CreateBookingRequest.of(
                        toCreate.propertyId(),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(15),
                        null
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.propertyId()).isEqualTo(toCreate.propertyId());
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                        }
                )
                .returnResult().getResponseBody();

        BookingDetailResponse booking3 = webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(CreateBookingRequest.of(
                        toCreate.propertyId(),
                        LocalDate.now().plusDays(15),
                        LocalDate.now().plusDays(25),
                        null
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.propertyId()).isEqualTo(toCreate.propertyId());
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                        }
                ).returnResult().getResponseBody();

        webTestClient
                .get()
                .uri("/bookings")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(BookingDetailResponse.class)
                .contains(booking1, booking2, booking3);

    }

    @Test
    void shouldCreateNewBookingWhenExistingOneCancelled() {
        var toCreate = CreateBookingRequest.of(
                Instant.now().toEpochMilli(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                5l
        );
        BookingDetailResponse toCancel = webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(toCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                        }
                )
                .returnResult().getResponseBody();

        webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(CreateBookingRequest.of(
                        toCreate.propertyId(),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(15),
                        9l
                ))
                .exchange()
                .expectStatus().is4xxClientError();

        webTestClient
                .put()
                .uri("/bookings/" + toCancel.id() + "/cancel")
                .exchange()
                .expectStatus().is2xxSuccessful();


        webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(CreateBookingRequest.of(
                        toCreate.propertyId(),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(15),
                        9l
                ))
                .exchange()
                .expectStatus().isCreated();

    }

    @Test
    void shouldRebookWhenReservationDateRangeIsAvailable() {
        var toCreate = CreateBookingRequest.of(
                Instant.now().toEpochMilli(),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                5l
        );
        BookingDetailResponse toCancel = webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(toCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingDetailResponse.class).value(booking -> {
                            assertThat(booking).isNotNull();
                            assertThat(booking.id()).isNotNull();
                            assertThat(booking.status()).isEqualTo(Status.ACTIVE);
                        }
                )
                .returnResult().getResponseBody();

        webTestClient
                .put()
                .uri("/bookings/" + toCancel.id() + "/cancel")
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .put()
                .uri("/bookings/" + toCancel.id() + "/rebook")
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .post()
                .uri("/bookings")
                .bodyValue(CreateBookingRequest.of(
                        toCreate.propertyId(),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(15),
                        9l
                ))
                .exchange()
                .expectStatus().is4xxClientError();

    }


}