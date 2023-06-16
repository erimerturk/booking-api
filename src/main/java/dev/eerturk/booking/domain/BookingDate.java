package dev.eerturk.booking.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "booking_date")
@ToString(exclude = "booking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class BookingDate {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull(message = "The booking date must be defined.")
    private LocalDate date;

    @Column
    @NotNull(message = "The booking date property id must be defined.")
    @Positive(message = "The booking date property id must be greater than zero")
    private Long propertyId;

    @ManyToOne
    @JoinColumn(name="booking_id", nullable=false)
    private Booking booking;

    @Column
    private BookingType bookingType;

}
