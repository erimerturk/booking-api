package dev.eerturk.booking.web;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CreateBookingValidationTests {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    var request = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), 666l);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenGuestIdNotDefinedThenValidationSucceeds() {
    var request = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), null);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenPropertyIdNotDefinedThenValidationFails() {
    var request = CreateBookingRequest.of(null, LocalDate.now(), LocalDate.now(), 666l);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).hasSize(1);
    List<String> constraintViolationMessages =
        violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(constraintViolationMessages).contains("The property id must be defined.");
  }

  @Test
  void whenPropertyIdNotDefinedButZeroThenValidationFails() {
    var request = CreateBookingRequest.of(0l, LocalDate.now(), LocalDate.now(), 666l);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).hasSize(1);
    List<String> constraintViolationMessages =
        violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(constraintViolationMessages).contains("The property id must be greater than zero");
  }

  @Test
  void whenGuestIdNotDefinedButZeroThenValidationFails() {
    var request = CreateBookingRequest.of(1l, LocalDate.now(), LocalDate.now(), 0l);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).hasSize(1);
    List<String> constraintViolationMessages =
        violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(constraintViolationMessages).contains("The guest id must be greater than zero");
  }

  @Test
  void whenStartDateIsNotDefinedThenValidationFails() {
    var request = CreateBookingRequest.of(1l, null, LocalDate.now(), 666l);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).hasSize(1);
    List<String> constraintViolationMessages =
        violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(constraintViolationMessages).contains("The booking start date must be defined.");
  }

  @Test
  void whenEndDateIsNotDefinedThenValidationFails() {
    var request = CreateBookingRequest.of(1l, LocalDate.now(), null, 666l);
    Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);
    assertThat(violations).hasSize(1);
    List<String> constraintViolationMessages =
        violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(constraintViolationMessages).contains("The booking end date must be defined.");
  }
}
