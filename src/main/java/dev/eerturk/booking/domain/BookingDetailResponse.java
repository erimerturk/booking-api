package dev.eerturk.booking.domain;

import java.time.LocalDate;

public record BookingDetailResponse(
        Long id,
        Long propertyId,
        LocalDate startDate,
        LocalDate endDate,
        Long guestId,
        Status status) {

}

