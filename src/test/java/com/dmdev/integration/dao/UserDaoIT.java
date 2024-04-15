package com.dmdev.integration.dao;

import com.dmdev.dao.UserDao;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.integration.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.dmdev.integration.util.TestObjectUtils.IVAN;
import static com.dmdev.integration.util.TestObjectUtils.KATE;
import static com.dmdev.integration.util.TestObjectUtils.PETR;
import static com.dmdev.integration.util.TestObjectUtils.SVETA;
import static com.dmdev.integration.util.TestObjectUtils.VLAD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoIT extends IntegrationTestBase {
  /*
   * Почти всегда в тестах в качестве полей выносится объект тестируемого класса:
   * - добавляет наглядности и прозрачности, что именно тестируем
   * - не нужно дублировать создание объекта в каждом методе
   * - можно легко настраивать, а также использовать Mock/Spy
   *   (например, в @BeforeEach или MockitoExtension)
   */
  private final UserDao userDao = UserDao.getInstance();

  @Test
  void shouldFindAllUsers() {
    var expectedResult = List.of(IVAN, PETR, SVETA, VLAD, KATE);
    var actualResult = userDao.findAll();
    assertThat(actualResult).hasSize(expectedResult.size());
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void shouldFindExistingEntity() {
    var actualResult = userDao.findById(IVAN.getId());
    var actualId = actualResult.orElseGet(User::new).getId();
    assertAll(
        () -> assertThat(actualResult).isPresent(),
        () -> assertEquals(IVAN.getId(), actualId)
    );
  }

  @Test
  void shouldReturnEmptyIfEntityDoesNotExist() {
    var actualResult = userDao.findById(TestObjectUtils.getMaxId() + 1);
    assertThat(actualResult).isEmpty();
  }

  @Test
  void shouldSaveCorrectlyEntity() {
    User user = User.builder()
        .name("Nikolay")
        .birthday(LocalDate.of(1991, 2, 26))
        .email("nikolay@gmail.com")
        .password("123")
        .role(Role.USER)
        .gender(Gender.MALE)
        .build();

    userDao.save(user);
    assertAll(
        () -> assertNotNull(user.getId()),
        () -> assertEquals(user, userDao.findById(user.getId()).orElseGet(User::new))
    );
  }

  @Test
  void shouldFindExistingUserByEmailAndPassword() {
    var actualResult = userDao.findByEmailAndPassword(PETR.getEmail(), PETR.getPassword());
    assertAll(
        () -> assertThat(actualResult).isPresent(),
        () -> assertThat(actualResult.get()).isEqualTo(PETR)
    );
  }

  @ParameterizedTest
  @MethodSource("getArgumentsToFind")
  void shouldNotFindUserWithWrongEmailOrPassword(String email, String password) {
    var actualResult = userDao.findByEmailAndPassword(email, password);
    assertThat(actualResult).isEmpty();
  }

  /**
   * @return arguments: email, password, expected error code
   */
  private static Stream<Arguments> getArgumentsToFind() {
    return Stream.of(
        Arguments.of("dummy", PETR.getPassword()),
        Arguments.of(PETR.getEmail(), "dummy"),
        Arguments.of("dummy", "dummy")
    );
  }

  @Test
  void shouldDeleteExistingUser() {
    assertTrue(userDao.delete(IVAN.getId()));
  }

  @Test
  void shouldNotDeleteNotExistingUser() {
    assertFalse(userDao.delete(TestObjectUtils.getMaxId() + 1));
  }

  @Test
  void shouldUpdateExistingEntity() {
    User expectedUser = userDao.findById(IVAN.getId()).orElseGet(User::new);
    expectedUser.setEmail("new@gmail.com");
    expectedUser.setPassword("pa$$w0rd");
    userDao.update(expectedUser);

    var actualUser = userDao.findById(IVAN.getId()).orElseGet(User::new);
    assertEquals(expectedUser, actualUser);
  }
}
