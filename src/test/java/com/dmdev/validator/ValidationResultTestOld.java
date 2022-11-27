package com.dmdev.validator;

import com.dmdev.dto.CreateUserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

@ExtendWith({
    MockitoExtension.class
})
public class ValidationResultTestOld
{
    private static final String VALID_LOCAL_DATE = "1991-02-26";
    private static final String INVALID_LOCAL_DATE = "Invalid local date";
    private static final String VALID_GENDER = Gender.MALE.name();
    private static final String INVALID_GENDER = "Invalid gender";
    private static final String VALID_ROLE = Role.USER.name();
    private static final String INVALID_ROLE = "Invalid role";

    @Mock
    CreateUserDto createUserDto;

    CreateUserValidator createUserValidator = CreateUserValidator.getInstance();

    @Test
    void shouldReturnValidResultIfNoErrorsOccurred()
    {
        ValidationResult validationResult = validateWith(VALID_LOCAL_DATE, VALID_GENDER, VALID_ROLE);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void shouldReturnInvalidResultIfBirthdayIsInvalid()
    {
        ValidationResult validationResult = validateWith(INVALID_LOCAL_DATE, VALID_GENDER, VALID_ROLE);

        assertAll(
            () -> assertThat(validationResult.isValid()).isFalse(),
            () -> assertThat(validationResult.getErrors())
                .containsOnly(Error.of("invalid.birthday", "Birthday is invalid"))
        );
    }

    @Test
    void shouldReturnInvalidResultIfGenderIsInvalid()
    {
        ValidationResult validationResult = validateWith(VALID_LOCAL_DATE, INVALID_GENDER, VALID_ROLE);

        assertAll(
            () -> assertThat(validationResult.isValid()).isFalse(),
            () -> assertThat(validationResult.getErrors())
                .containsOnly(Error.of("invalid.gender", "Gender is invalid"))
        );
    }

    @Test
    void shouldReturnInvalidResultIfRoleIsInvalid()
    {
        ValidationResult validationResult = validateWith(VALID_LOCAL_DATE, VALID_GENDER, INVALID_ROLE);

        assertAll(
            () -> assertThat(validationResult.isValid()).isFalse(),
            () -> assertThat(validationResult.getErrors())
                .containsOnly(Error.of("invalid.role", "Role is invalid"))
        );
    }

    private ValidationResult validateWith(String localDate, String gender, String role)
    {
        doReturn(localDate).when(createUserDto).getBirthday();
        doReturn(gender).when(createUserDto).getGender();
        doReturn(role).when(createUserDto).getRole();

        return createUserValidator.validate(createUserDto);
    }
}
