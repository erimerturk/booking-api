package dev.eerturk.booking.web;

import dev.eerturk.booking.dto.BookingDetailResponse;
import dev.eerturk.booking.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTests {

    @Autowired
    private JacksonTester<BookingDetailResponse> responseJson;

    @Autowired
    private JacksonTester<CreateBookingRequest> requestJson;

    @Test
    void testSerialize() throws Exception {
        var booking = new BookingDetailResponse(1l, 2222l, LocalDate.now(), LocalDate.now().plusDays(5l), 2111l, Status.ACTIVE);
        var jsonContent = responseJson.write(booking);
        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .satisfies(number -> assertThat(number.longValue()).isEqualTo(booking.id().longValue()));

        assertThat(jsonContent).extractingJsonPathNumberValue("@.propertyId", Long.class)
                .satisfies(number -> assertThat(number.longValue()).isEqualTo(booking.propertyId().longValue()));

        assertThat(jsonContent).extractingJsonPathNumberValue("@.guestId", Long.class)
                .satisfies(number -> assertThat(number.longValue()).isEqualTo(booking.guestId().longValue()));

        assertThat(jsonContent).extractingJsonPathValue("@.status")
                .satisfies(data -> assertThat(Status.valueOf((String) data)).isEqualTo(booking.status()));


        assertThat(jsonContent).extractingJsonPathValue("@.startDate")
                .satisfies(data -> assertThat(LocalDate.parse((String) data)).isEqualTo(booking.startDate()));

        assertThat(jsonContent).extractingJsonPathValue("@.endDate")
                .satisfies(data -> assertThat(LocalDate.parse((String) data)).isEqualTo(booking.endDate()));
    }

    @Test
    void testDeserialize() throws Exception {

        LocalDate initialDate = LocalDate.of(2023, 06, 16);
        var content = """
                {
                    "propertyId":2222,
                    "startDate":"2023-06-16",
                    "endDate":"2023-06-21",
                    "guestId":2111
                }
                """;
        assertThat(requestJson.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(CreateBookingRequest.of(2222l, initialDate, initialDate.plusDays(5l), 2111l));
    }

}
