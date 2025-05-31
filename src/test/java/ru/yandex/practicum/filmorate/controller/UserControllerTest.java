package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void create_whenUserIsValid_returnsUserWithId() {
        int expectedId = 1;

        var actual = userController.create(new User(0L, "mail@mail.ru", "dolore",
                "Nick Name", LocalDate.parse("1946-08-20")));

        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
    }

    @Test
    public void create_whenEmailDuplicate_throwsException() {
        var createdUser = userController.create(new User(0L, "test@mail.ru", "login",
                "Nick", null));
        String email = createdUser.getEmail();
        var testUser = new User(0L, email, "1_login", "Bob", null);

        assertThrows(DuplicateEmailException.class, () -> {
            userController.create(testUser);
        });
    }

    @Test
    public void create_whenLoginDuplicate_throwsException() {
        var createdUser = userController.create(new User(0L, "a@mail.ru", "test_login",
                "Nick", null));
        String login = createdUser.getLogin();
        var testUser = new User(0L, "b@gmail.com", login, "Bob", null);

        assertThrows(DuplicateLoginException.class, () -> {
            userController.create(testUser);
        });
    }

    @Test
    public void create_whenNameNullOrEmpty_shouldSaveLoginInNameField() {
        var user = new User(0L, "a@mail.ru", "login_name",
                null, LocalDate.parse("1946-08-20"));
        String login = user.getLogin();

        var createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals(login, createdUser.getName());
    }

    @Test
    public void getAll_whenNoUsers_returnsEmptyList() {
        int expectedSize = 0;

        var actual = userController.getAll();

        assertNotNull(actual);
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void getAll_whenThereIsUser_returnsListWithUser() {
        int expectedSize = 1;
        userController.create(new User(0L, "e@mail.ru", "login", "name", null));

        var actual = userController.getAll();

        assertNotNull(actual);
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void update_whenIdNotValid_throwsException() {
        var createdUser = userController.create(new User(0L, "e@mail.ru", "login",
                null, null));
        // создаем User с id, которого нет в мапе контроллера
        long notValidId = createdUser.getId() + 1;
        var updatedUser = new User(notValidId, "e@mail.ru", "login", null, null);

        assertThrows(UserNotFoundException.class, () -> {
            userController.update(updatedUser);
        });
    }

    @Test
    public void update_whenIdValid_returnsUpdatedUser() {
        var oldUser = userController.create(new User(1L, "mail@mail.ru", "dolore",
                "Nick Name", LocalDate.parse("1946-08-20")));
        // создаем User с id, который есть в мапе контроллера
        // и задаем ему другие поля
        long validId = oldUser.getId();
        var newUser = new User(validId, "yandex@yandex.ru", "carcosa",
                "Rust Cole", LocalDate.parse("2001-09-11"));

        userController.update(newUser);

        assertEquals(newUser.getEmail(), oldUser.getEmail());
        assertEquals(newUser.getLogin(), oldUser.getLogin());
        assertEquals(newUser.getName(), oldUser.getName());
        assertEquals(newUser.getBirthday(), oldUser.getBirthday());
    }

    @Test
    public void update_whenFieldNotValid_shouldNotUpdateField() {
        var oldUser = userController.create(new User(1L, "mail@mail.ru", "dolore",
                "Nick Name", LocalDate.parse("1946-08-20")));
        // создаем User с id, который есть в мапе контроллера
        // значение его полей login и birthday будут невалидными
        long validId = oldUser.getId();
        var newUser = new User(validId, "yandex@yandex.ru", "log in",
                "Tyler Durden", LocalDate.parse("2446-08-20"));

        userController.update(newUser);

        assertEquals(newUser.getEmail(), oldUser.getEmail());
        assertNotEquals(newUser.getLogin(), oldUser.getLogin());
        assertEquals(newUser.getName(), oldUser.getName());
        assertNotEquals(newUser.getBirthday(), oldUser.getBirthday());
    }
}