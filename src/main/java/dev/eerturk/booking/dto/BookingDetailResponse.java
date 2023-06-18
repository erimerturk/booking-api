package dev.eerturk.booking.dto;

import dev.eerturk.booking.model.Status;

import java.time.LocalDate;

public record BookingDetailResponse(
        Long id,
        Long propertyId,
        LocalDate startDate,
        LocalDate endDate,
        Long guestId,
        Status status) {

}

