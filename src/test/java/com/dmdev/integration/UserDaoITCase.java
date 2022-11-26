package com.dmdev.integration;

import com.dmdev.dao.UserDao;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.util.ConnectionPool;
import com.dmdev.util.LocalDateFormatter;
import com.dmdev.util.RecordsUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserDaoITCase extends IntegrationTestBase
{
    private static final User PETR = User.builder()
        .id(2)
        .name("Petr")
        .birthday(LocalDateFormatter.format("1995-10-19"))
        .email("petr@gmail.com")
        .password("123")
        .role(Role.USER)
        .gender(Gender.MALE)
        .build();

    private static final User NIKOLAY = User.builder()
        .name("Nikolay")
        .birthday(LocalDateFormatter.format("1991-02-26"))
        .email("nikolay@gmail.com")
        .password("123")
        .role(Role.USER)
        .gender(Gender.MALE)
        .build();

    UserDao userDao = UserDao.getInstance();

    @Test
    void shouldReturnAllUsers()
    {
        assertThat(userDao.findAll()).hasSize(RecordsUtil.getRowsNumber());
    }

    @Test
    void shouldReturnUserById()
    {
        assertThat(userDao.findById(PETR.getId()).orElseGet(User::new)).isEqualTo(PETR);
    }

    @Test
    void shouldReturnEmptyUserIfIdNotExist()
    {
        assertThat(userDao.findById(RecordsUtil.getMaxId() + 1)).isEmpty();
    }

    @Test
    void shouldReturnNewUserAfterSave()
    {
        int rowsNumber = RecordsUtil.getRowsNumber();

        assertAll(
            () -> assertThat(userDao.save(NIKOLAY)).isEqualTo(NIKOLAY),
            () -> assertThat(RecordsUtil.getRowsNumber()).isEqualTo(rowsNumber + 1)
        );
    }

    @Test
    void shouldReturnUserByEmailAndPassword()
    {
        assertThat(userDao.findByEmailAndPassword(PETR.getEmail(), PETR.getPassword())
            .orElseGet(User::new)).isEqualTo(PETR);
    }

    @Test
    void shouldReturnEmptyUserIfEmailAndPasswordNotExist()
    {
        assertThat(userDao.findByEmailAndPassword("email", "password")).isEmpty();
    }

    @Test
    void shouldReturnTrueIfUserDeletedAndFalseIfUserWasNotFound()
    {
        assertAll(
            () -> assertThat(userDao.delete(PETR.getId())).isTrue(),
            () -> assertThat(userDao.delete(NIKOLAY.getId())).isFalse()
        );
    }

    @Test
    void shouldUpdateUserToAnother()
    {
        User petrWithNewData = User.builder()
            .id(PETR.getId())
            .name(PETR.getName())
            .birthday(PETR.getBirthday())
            .email("example@gmail.com")
            .password("pa$$w0rd")
            .role(Role.USER)
            .gender(Gender.MALE)
            .build();

        assertAll(
            () -> assertThat(petrWithNewData).isNotEqualTo(PETR),
            () -> assertDoesNotThrow(() -> userDao.update(petrWithNewData)),
            () -> assertThat(userDao.findById(PETR.getId()).orElseGet(User::new))
                .isEqualTo(petrWithNewData)
        );
    }

    @AfterAll
    static void closeConnectionPool()
    {
        ConnectionPool.close();
    }
}
