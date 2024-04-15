package com.dmdev.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalDateFormatterTest {
  @Test
  void shouldFormatLocalDateCorrectly() {
    var sourceDate = "2019-05-13";

    var actualResult = LocalDateFormatter.format(sourceDate);
    assertThat(actualResult).isEqualTo(LocalDate.of(2019, 5, 13));
  }

  @Test
  void shouldThrowExceptionIfDateIsInvalid() {
    var invalidDate = "05-13-2020";

    assertThrows(DateTimeParseException.class, () -> LocalDateFormatter.format(invalidDate));
  }

  @Test
  void shouldReturnTrueIfDateIsValid() {
    var sourceDate = "2019-05-13";
    assertTrue(LocalDateFormatter.isValid(sourceDate));
  }

  @Test
  void shouldReturnFalseIfDateIsInvalid() {
    var sourceDate = "dummy";
    assertFalse(LocalDateFormatter.isValid(sourceDate));
  }

  @Test
  void shouldReturnFalseIfDateIsNull() {
    assertFalse(LocalDateFormatter.isValid(null));
  }

  @ParameterizedTest
  @MethodSource("getValidationArguments")
  void isValid(String date, boolean expectedResult) {
    var actualResult = LocalDateFormatter.isValid(date);
    assertThat(expectedResult).isEqualTo(actualResult);
  }

  static Stream<Arguments> getValidationArguments() {
    return Stream.of(
        Arguments.of("2019-05-13", true),
        Arguments.of("dummy", false),
        Arguments.of("01-01-2001", false),
        Arguments.of("2020-11-28 12:25", false),
        Arguments.of(null, false)
    );
  }
}
