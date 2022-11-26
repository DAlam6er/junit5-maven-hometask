package com.dmdev.mapper;

import com.dmdev.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserMapperTest
{
    private static final Integer USER_ID = 1;
    private static final String USER_NAME = "Ivan";

    User user;
    UserMapper userMapper = UserMapper.getInstance();

    @Test
    void shouldReturnUserDto()
    {
        user = User.builder()
            .id(USER_ID)
            .name(USER_NAME)
            .build();

        var userDto = userMapper.map(user);
        assertAll(
            () -> assertThat(userDto.getId()).isEqualTo(USER_ID),
            () -> assertThat(userDto.getName()).isEqualTo(USER_NAME),
            () -> assertThat(userDto.getRole()).isNull()
        );
    }

    @Test
    void shouldThrowNPEIfUserIsNull()
    {
        assertThrows(NullPointerException.class, () -> userMapper.map(user));
    }
}
