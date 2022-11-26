package com.dmdev.mapper;

import com.dmdev.dto.CreateUserDto;
import com.dmdev.util.LocalDateFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateUserMapperTest
{
    private static final String USER_NAME = "Ivan";
    private static final String USER_BIRTHDAY = "1991-02-26";

    CreateUserMapper createUserMapper = CreateUserMapper.getInstance();
    CreateUserDto createUserDto;

    @Test
    void shouldReturnUserDto()
    {
        createUserDto = CreateUserDto.builder()
            .name(USER_NAME)
            .birthday(USER_BIRTHDAY)
            .build();

        var user = createUserMapper.map(createUserDto);
        assertAll(
            () -> assertThat(user.getName()).isEqualTo(USER_NAME),
            () -> assertThat(user.getBirthday()).isEqualTo(LocalDateFormatter.format(USER_BIRTHDAY)),
            () -> assertThat(user.getRole()).isNull()
        );
    }

    @Test
    void shouldThrowNPEIfUserIsNull()
    {
        assertThrows(NullPointerException.class, () -> createUserMapper.map(createUserDto));
    }
}
