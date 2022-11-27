package com.dmdev.service;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.mapper.CreateUserMapper;
import com.dmdev.mapper.UserMapper;
import com.dmdev.validator.CreateUserValidator;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.dmdev.integration.util.TestObjectUtils.IVAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
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
    void shouldCallDaoAndConvertEntityOnLogin()
    {
        UserDto expectedUserDto = UserDto.builder().build();

        doReturn(expectedUserDto).when(userMapper).map(IVAN);
        doReturn(Optional.of(IVAN)).when(userDao).findByEmailAndPassword(IVAN.getEmail(), IVAN.getPassword());

        var actualResult = userService.login(IVAN.getEmail(), IVAN.getPassword());

        assertThat(actualResult).isPresent();
        assertSame(expectedUserDto, actualResult.get());
        verify(userDao).findByEmailAndPassword(IVAN.getEmail(), IVAN.getPassword());
        verify(userMapper).map(IVAN);
    }

    @Test
    void shouldValidateInputAndConvertSavedEntity()
    {
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
}
