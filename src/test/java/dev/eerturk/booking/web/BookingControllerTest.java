package dev.eerturk.booking.web;

import dev.eerturk.booking.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void whenDeleteBookingNotExistingThenShouldReturn404() throws Exception {
        var id = 333l;

        doThrow(BookingNotFoundException.class).when(bookingService)
                .delete(id);
        mockMvc
                .perform(delete("/bookings/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenRebookingDateNotAvailableThenShouldReturn422() throws Exception {
        var id = 333l;

        doThrow(ReservationAlreadyExistsException.class).when(bookingService)
                .rebook(id);
        mockMvc
                .perform(delete("/bookings/" + id + "/rebook"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenCreateBookingSuccessfullyThenShouldReturn200() throws Exception {
        var request = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), 666l);
        BookingDetailResponse expected = new BookingDetailResponse(
                1111l,
                request.propertyId(),
                request.startDate(),
                request.endDate(),
                request.guestId(),
                Status.ACTIVE
        );
        given(bookingService.create(request)).willReturn(expected);
        mockMvc
                .perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void whenCreateBookingFailedOnValidationsThenShouldReturn400() throws Exception {
        var request = CreateBookingRequest.of(1l, null, LocalDate.now(), 666l);
        mockMvc
                .perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}