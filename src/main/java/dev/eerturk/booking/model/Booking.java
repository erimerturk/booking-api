package dev.eerturk.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "booking")
@ToString(exclude = "dates")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

  @Id @GeneratedValue private Long id;

  @Column
  @NotNull(message = "The property id must be defined.")
  @Positive(message = "The property id must be greater than zero")
  private Long propertyId;

  @Column
  @NotNull(message = "The booking start date must be defined.")
  private LocalDate startDate;

  @Column
  @NotNull(message = "The booking end date must be defined.")
  private LocalDate endDate;

  @Column private Long guestId;

  @OneToMany(
      mappedBy = "booking",
      cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE},
      orphanRemoval = true)
  private List<BookingDate> dates = new ArrayList<>();

  @Column
  @NotNull(message = "The booking status must be defined.")
  private Status status;

  @Column private BookingType bookingType;

  public boolean isBlock() {
    return bookingType == BookingType.BLOCK;
  }

  public void initDates() {
    List<BookingDate> bookingDates =
        getStartDate()
            .datesUntil(getEndDate().plusDays(1))
            .map(
                date -> {
                  BookingDate bookingDate = new BookingDate();
                  bookingDate.setDate(date);
                  bookingDate.setBooking(this);
                  bookingDate.setBookingType(getBookingType());
                  bookingDate.setPropertyId(getPropertyId());
                  return bookingDate;
                })
            .collect(Collectors.toList());
    this.dates.addAll(bookingDates);
  }

  public void deleteDates() {
    this.dates.clear();
  }
}
