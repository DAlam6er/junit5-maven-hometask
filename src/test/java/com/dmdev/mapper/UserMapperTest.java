package com.dmdev.mapper;

import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest
{
    private final UserMapper mapper = UserMapper.getInstance();

    @Test
    void shouldMapAllFieldsCorrectly()
    {
        User user = User.builder()
            .id(1)
            .name("Test")
            .birthday(LocalDate.of(2000, 5, 18))
            .email("test@gmail.com")
            .password("pass")
            .gender(Gender.MALE)
            .role(Role.USER)
            .build();

        UserDto actualResult = mapper.map(user);

        assertAll(
            () -> assertEquals(user.getId(), actualResult.getId()),
            () -> assertEquals(user.getName(), actualResult.getName()),
            () -> assertEquals(user.getBirthday(), actualResult.getBirthday()),
            () -> assertEquals(user.getEmail(), actualResult.getEmail()),
            () -> assertSame(user.getGender(), actualResult.getGender()),
            () -> assertSame(user.getRole(), actualResult.getRole())
        );
    }
}
