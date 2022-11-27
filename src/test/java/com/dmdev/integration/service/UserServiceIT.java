package com.dmdev.integration.service;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.exception.ValidationException;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateUserMapper;
import com.dmdev.mapper.UserMapper;
import com.dmdev.service.UserService;
import com.dmdev.validator.CreateUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.dmdev.integration.util.TestObjectUtils.IVAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class UserServiceIT_ extends IntegrationTestBase
{
    UserService userService;

    @BeforeEach
    void init()
    {
        userService = new UserService(
            CreateUserValidator.getInstance(),
            UserDao.getInstance(),
            CreateUserMapper.getInstance(),
            UserMapper.getInstance()
        );
    }

    @Test
    void shouldLoginSuccessfully()
    {
        var actualResult = userService.login(IVAN.getEmail(), IVAN.getPassword());

        assertAll(
            () -> assertThat(actualResult).isPresent(),
            () -> assertEquals(actualResult.orElse(UserDto.builder().build()).getEmail(), IVAN.getEmail())
        );

    }

    @ParameterizedTest
    @MethodSource("getArgumentsForLogin")
    void shouldNotLoginIfPasswordOrEmailIsNotCorrect(String email, String password)
    {
        var actualResult = userService.login(email, password);
        assertThat(actualResult).isEmpty();
    }

    @Test
    void shouldCreateCorrectEntity()
    {
        var userToCreate = CreateUserDto.builder()
            .name("Nikolay")
            .birthday("1991-02-26")
            .email("nikolay@gmail.com")
            .password("123")
            .role("USER")
            .gender("MALE")
            .build();

/*        var userDto = UserDto.builder()
            .id(TestObjectUtils.getMaxId() + 1)
            .name(userToCreate.getName())
            .birthday(LocalDateFormatter.format(userToCreate.getBirthday()))
            .email(userToCreate.getEmail())
            .role(Role.find(userToCreate.getRole()).orElse(null))
            .gender(Gender.find(userToCreate.getGender()).orElse(null))
            .build();
        assertThat(userService.create(userToCreate)).isEqualTo(userDto);*/

        var createdUser = userService.create(userToCreate);

        assertNotNull(createdUser.getId());
    }

    @ParameterizedTest
    @MethodSource("getWrongArgumentsForEntityCreate")
    void shouldThrowValidationExceptionIfEntityInvalid
        (String birthday, String role, String gender, String expectedErrorCode)
    {
        var userToCreate = CreateUserDto.builder()
            .name("Nikolay")
            .birthday(birthday)
            .email("nikolay@gmail.com")
            .password("123")
            .role(role)
            .gender(gender)
            .build();

        var actualException = assertThrows(ValidationException.class, () -> userService.create(userToCreate));
        assertThat(actualException.getErrors()).hasSize(1);
        assertThat(actualException.getErrors().get(0).getCode()).isEqualTo(expectedErrorCode);
    }

    /**
     *
     * @return arguments: email, password
     */
    private static Stream<Arguments> getArgumentsForLogin()
    {
        return Stream.of(
            Arguments.of("dummy", "dummy"),
            Arguments.of(IVAN.getEmail(), "dummy"),
            Arguments.of("dummy", IVAN.getPassword())
        );
    }

    /**
     *
     * @return arguments: birthday, role, gender, expectedErrorCode
     */
    private static Stream<Arguments> getWrongArgumentsForEntityCreate()
    {
        return Stream.of(
            Arguments.of("09-25-2000", Role.ADMIN.name(), Gender.FEMALE.name(), "invalid.birthday"),
            Arguments.of("2000-09-25", Role.ADMIN.name(), "dummy", "invalid.gender"),
            Arguments.of("2000-09-25", "dummy", Gender.MALE.name(), "invalid.role")
        );
    }
}
