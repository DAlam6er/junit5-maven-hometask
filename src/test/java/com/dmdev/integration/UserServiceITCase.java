package com.dmdev.integration;

import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.UserMapper;
import com.dmdev.service.UserService;
import com.dmdev.util.LocalDateFormatter;
import com.dmdev.util.RecordsUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceITCase extends IntegrationTestBase
{
    UserMapper userMapper = UserMapper.getInstance();
    UserService userService = UserService.getInstance();

    @Test
    void shouldReturnUserDtoIfEmailAndPasswordIsCorrect()
    {
        var PETR = User.builder()
            .id(2)
            .name("Petr")
            .birthday(LocalDateFormatter.format("1995-10-19"))
            .email("petr@gmail.com")
            .password("123")
            .role(Role.USER)
            .gender(Gender.MALE)
            .build();

        var maybeUserDto = Optional.of(PETR).map(userMapper::map);

        assertAll(
            () -> assertThat(userService.login(PETR.getEmail(), PETR.getPassword())).isEqualTo(maybeUserDto),
            () -> assertThat(userService.login("dummy", "dummy")).isEmpty(),
            () -> assertThat(userService.login("dummy", PETR.getPassword())).isEmpty(),
            () -> assertThat(userService.login(PETR.getEmail(), "dummy")).isEmpty()
        );
    }

    @Test
    void shouldCreateUserIfValidationPassed()
    {
        var createUserDto = CreateUserDto.builder()
            .name("Nikolay")
            .birthday("1991-02-26")
            .email("nikolay@gmail.com")
            .password("123")
            .role("USER")
            .gender("MALE")
            .build();

        var userDto = UserDto.builder()
            .id(RecordsUtil.getMaxId() + 1)
            .name(createUserDto.getName())
            .birthday(LocalDateFormatter.format(createUserDto.getBirthday()))
            .email(createUserDto.getEmail())
            .role(Role.find(createUserDto.getRole()).orElse(null))
            .gender(Gender.find(createUserDto.getGender()).orElse(null))
            .build();

        assertThat(userService.create(createUserDto)).isEqualTo(userDto);
    }

    @Test
    void shouldThrowExceptionIfValidationFails()
    {
        var createUserDto = CreateUserDto.builder()
            .name("Nikolay")
            .birthday("dummy")
            .email("nikolay@gmail.com")
            .password("123")
            .role("USER")
            .gender("MALE")
            .build();

        var createUserDto2 = CreateUserDto.builder()
            .name("Nikolay")
            .birthday("1991-02-26")
            .email("nikolay@gmail.com")
            .password("123")
            .role("dummy")
            .gender("MALE")
            .build();

        var createUserDto3 = CreateUserDto.builder()
            .name("Nikolay")
            .birthday("1991-02-26")
            .email("nikolay@gmail.com")
            .password("123")
            .role("USER")
            .gender("dummy")
            .build();

        assertAll(
            () ->  assertThrows(ValidationException.class, () -> userService.create(createUserDto)),
            () ->  assertThrows(ValidationException.class, () -> userService.create(createUserDto2)),
            () ->  assertThrows(ValidationException.class, () -> userService.create(createUserDto3))
        );
    }
}
