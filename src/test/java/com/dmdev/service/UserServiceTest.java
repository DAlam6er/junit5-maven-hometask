package com.dmdev.service;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateUserMapper;
import com.dmdev.mapper.UserMapper;
import com.dmdev.validator.CreateUserValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.dmdev.integration.util.TestObjectUtils.IVAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private CreateUserValidator createUserValidator;
  @Mock
  private UserDao userDao;
  @Mock
  private CreateUserMapper createUserMapper;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private UserService userService;

  @Test
  void shouldCallDaoAndConvertEntityOnLogin() {
    UserDto expectedUserDto = UserDto.builder().build();

    doReturn(Optional.of(IVAN)).when(userDao).findByEmailAndPassword(IVAN.getEmail(), IVAN.getPassword());
    doReturn(expectedUserDto).when(userMapper).map(IVAN);

    var actualResult = userService.login(IVAN.getEmail(), IVAN.getPassword());

    assertThat(actualResult).isPresent();
    assertSame(expectedUserDto, actualResult.get());
    verify(userDao).findByEmailAndPassword(IVAN.getEmail(), IVAN.getPassword());
    verify(userMapper).map(IVAN);
  }

  @Test
  void shouldFailLogin() {
    doReturn(Optional.empty()).when(userDao).findByEmailAndPassword(any(), any());

    var actualResult = userService.login("dummy", "123");
    assertThat(actualResult).isEmpty();
    verifyNoInteractions(userMapper);
  }

  @Test
  void shouldValidateInputAndConvertSavedEntity() {
    var createUserDto = CreateUserDto.builder().build();
    var expectedResult = UserDto.builder().build();
    doReturn(new ValidationResult()).when(createUserValidator).validate(createUserDto);
    doReturn(expectedResult).when(userMapper).map(any());

    var actualResult = userService.create(createUserDto);

    assertSame(actualResult, expectedResult);
    verify(createUserValidator).validate(createUserDto);
    verify(createUserMapper).map(createUserDto);
    verify(userDao).save(any());
  }

  @Test
  void shouldValidateInput(){
    var createUserDto = CreateUserDto.builder()
        .name("Ivan")
        .email("test@gmail.com")
        .password("123")
        .birthday("2000-01-01")
        .role(Role.USER.name())
        .gender(Gender.MALE.name())
        .build();

    UserDto userDto = UserDto.builder()
        .id(1)
        .name("Test")
        .birthday(LocalDate.of(2000, 5, 18))
        .email("test@gmail.com")
        .gender(Gender.MALE)
        .role(Role.USER)
        .build();

    doReturn(new ValidationResult()).when(createUserValidator).validate(createUserDto);
    doReturn(IVAN).when(createUserMapper).map(createUserDto);
    doReturn(userDto).when(userMapper).map(IVAN);

    var actualResult = userService.create(createUserDto);
    assertThat(actualResult).isEqualTo(userDto);
    verify(userDao).save(IVAN);
  }

  @Test
  void shouldThrowValidationExceptionWhenValidationFails() {
    var createUserDto = CreateUserDto.builder().build();

    var validationResult = new ValidationResult();
    validationResult.add(Error.of("invalid.data", "Invalid data"));
    doReturn(validationResult).when(createUserValidator).validate(createUserDto);
    assertThrows(ValidationException.class, () -> userService.create(createUserDto));
    verifyNoInteractions(userDao, createUserMapper, userDao);
  }
}
