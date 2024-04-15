package com.dmdev.integration.util;

import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.util.ConnectionPool;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

/**
 * В разных тестовых классах часто используется одна и та же модель и даже методы.
 * Особенно часто выносятся в константы данные из тестовых SQL скриптов.
 * <p>
 * Поэтому проще и лучше выносить все общее в отдельные тестовые утилитные классы
 */
@UtilityClass
public class TestObjectUtils {
  public static final User IVAN = User.builder()
      .id(1)
      .name("Ivan")
      .birthday(LocalDate.of(1990, 1, 10))
      .email("ivan@gmail.com")
      .password("111")
      .role(Role.ADMIN)
      .gender(Gender.MALE)
      .build();

  public static final User PETR = User.builder()
      .id(2)
      .name("Petr")
      .birthday(LocalDate.of(1995, 10, 19))
      .email("petr@gmail.com")
      .password("123")
      .role(Role.USER)
      .gender(Gender.MALE)
      .build();

  public static final User SVETA = User.builder()
      .id(3)
      .name("Sveta")
      .birthday(LocalDate.of(2001, 12, 23))
      .email("sveta@gmail.com")
      .password("321")
      .role(Role.USER)
      .gender(Gender.FEMALE)
      .build();

  public static final User VLAD = User.builder()
      .id(4)
      .name("Vlad")
      .birthday(LocalDate.of(1984, 3, 14))
      .email("vlad@gmail.com")
      .password("456")
      .role(Role.USER)
      .gender(Gender.MALE)
      .build();

  public static final User KATE = User.builder()
      .id(5)
      .name("Kate")
      .birthday(LocalDate.of(1989, 8, 9))
      .email("kate@gmail.com")
      .password("777")
      .role(Role.ADMIN)
      .gender(Gender.FEMALE)
      .build();

  @SneakyThrows
  public int getRowsNumber() {
    try (var connection = ConnectionPool.get();
         var preparedStatement =
             connection.prepareStatement("SELECT COUNT(*) FROM users")) {
      var resultSet = preparedStatement.executeQuery();
      resultSet.next();
      return resultSet.getInt(1);
    }
  }

  @SneakyThrows
  public int getMaxId() {
    try (var connection = ConnectionPool.get();
         var preparedStatement =
             connection.prepareStatement("SELECT max(id) FROM users")) {
      var resultSet = preparedStatement.executeQuery();
      resultSet.next();
      return resultSet.getInt(1);
    }
  }
}
