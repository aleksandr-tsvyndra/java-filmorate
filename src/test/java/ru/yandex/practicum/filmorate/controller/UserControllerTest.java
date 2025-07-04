package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dal.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    public void create_whenUserIsValid_returnsUserWithId() {
        int expectedId = 1;

        var actual = userController.create(new NewUserRequest("mail@mail.ru", "dolore",
                "Nick Name", LocalDate.parse("1946-08-20")));

        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
    }

    @Test
    public void create_whenEmailDuplicate_throwsException() {
        var createdUser = userController.create(new NewUserRequest( "test", "login",
                "test@mail.ru", null));
        String email = createdUser.getEmail();
        var testUser = new NewUserRequest(email, "1_login", "test@mail.ru", null);

        assertThrows(DuplicateEmailException.class, () -> {
            userController.create(testUser);
        });
    }

    @Test
    public void create_whenLoginDuplicate_throwsException() {
        var createdUser = userController.create(new NewUserRequest("test_login", "test_login",
                "a@mail.ru", null));
        String login = createdUser.getLogin();
        var testUser = new NewUserRequest(login, "Bob", "b@gmail.com", null);

        assertThrows(DuplicateLoginException.class, () -> {
            userController.create(testUser);
        });
    }

    @Test
    public void create_whenNameNullOrEmpty_shouldSaveLoginInNameField() {
        var user = new NewUserRequest("login_name", null,
                "a@mail.ru", LocalDate.parse("1946-08-20"));
        String login = user.getLogin();

        var createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals(login, createdUser.getName());
    }

    @Test
    public void getById_whenUserPresent_returnsUser() {
        var createdUser = userController.create(new NewUserRequest("a@mail.ru", "login",
                "name", LocalDate.parse("1950-08-20")));
        long presentId = createdUser.getId();

        var actual = userController.getById(presentId);

        assertNotNull(actual);
        assertEquals(createdUser.getEmail(), actual.getEmail());
        assertEquals(createdUser.getLogin(), actual.getLogin());
        assertEquals(createdUser.getName(), actual.getName());
        assertEquals(createdUser.getBirthday(), actual.getBirthday());
    }

    @Test
    public void getById_whenUserNotPresent_throwsException() {
        var createdUser = userController.create(new NewUserRequest("a@mail.ru", "login",
                "name", LocalDate.parse("1950-08-20")));
        long notPresentId = createdUser.getId() + 1;

        assertThrows(NotFoundException.class, () -> {
            userController.getById(notPresentId);
        });
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
        userController.create(new NewUserRequest("e@mail.ru", "login", "name", null));

        var actual = userController.getAll();

        assertNotNull(actual);
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void update_whenIdNotValid_throwsException() {
        var createdUser = userController.create(new NewUserRequest("e@mail.ru", "login",
                "a@mail.ru", null));
        // создаем User с id, которого нет в мапе контроллера
        long notValidId = createdUser.getId() + 1;
        var updatedUser = new UpdateUserRequest(notValidId, "e@mail.ru", "login", null, null);

        assertThrows(NotFoundException.class, () -> {
            userController.update(updatedUser);
        });
    }

    @Test
    public void update_whenIdValid_returnsUpdatedUser() {
        var oldUser = userController.create(new NewUserRequest("mail@mail.ru", "dolore",
                "Nick Name", LocalDate.parse("1946-08-20")));
        // создаем User с id, который есть в мапе контроллера
        // и задаем ему другие поля
        long validId = oldUser.getId();
        var newUser = new UpdateUserRequest(validId, "yandex@yandex.ru", "carcosa",
                "Rust Cole", LocalDate.parse("2001-09-11"));

        userController.update(newUser);

        assertEquals(newUser.getEmail(), oldUser.getEmail());
        assertEquals(newUser.getLogin(), oldUser.getLogin());
        assertEquals(newUser.getName(), oldUser.getName());
        assertEquals(newUser.getBirthday(), oldUser.getBirthday());
    }

    @Test
    public void update_whenFieldNotValid_shouldNotUpdateField() {
        var oldUser = userController.create(new NewUserRequest("mail@mail.ru", "dolore",
                "Nick Name", LocalDate.parse("1946-08-20")));
        // создаем User с id, который есть в мапе контроллера
        // значение его полей login и birthday будут невалидными
        long validId = oldUser.getId();
        var newUser = new UpdateUserRequest(validId, "yandex@yandex.ru", "log in",
                "Tyler Durden", LocalDate.parse("2446-08-20"));

        userController.update(newUser);

        assertEquals(newUser.getEmail(), oldUser.getEmail());
        assertNotEquals(newUser.getLogin(), oldUser.getLogin());
        assertEquals(newUser.getName(), oldUser.getName());
        assertNotEquals(newUser.getBirthday(), oldUser.getBirthday());
    }

    @Test
    public void addFriend_returnsUserWithFriendAdded() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var friend = userController.create(new NewUserRequest( "yandex@mail.ru", "cusco",
                "nick", LocalDate.parse("1992-10-25")));
        var userId = user.getId();
        var friendId = friend.getId();

        userController.addFriend(userId, friendId);

        assertTrue(user.getFriends().contains(friendId));
        assertTrue(friend.getFriends().isEmpty());
    }

    @Test
    public void addFriend_whenUserNotPresent_throwsException() {
        var friend = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var friendId = friend.getId();
        var userId = friend.getId() + 1;

        assertThrows(NotFoundException.class, () -> {
            userController.addFriend(userId, friendId);
        });
    }

    @Test
    public void getUserFriends_returnsUserFriendsCollection() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var userId = user.getId();

        var friend1 = userController.create(new NewUserRequest( "m@mail.ru", "cusco",
                "phil", LocalDate.parse("1997-02-12")));
        var friend2 = userController.create(new NewUserRequest( "g@mail.ru", "pato",
                "alice", LocalDate.parse("1999-01-21")));

        // добавляем юзеру с id 1 двух друзей с id 2 и id 3
        userController.addFriend(userId, friend1.getId());
        userController.addFriend(userId, friend2.getId());

        assertEquals(2, user.getFriends().size());

        var userFriends = userController.getUserFriends(userId);

        assertNotNull(userFriends);
        assertEquals(2, userFriends.size());
    }

    @Test
    public void getUserFriends_whenUserNotPresent_throwsException() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var notPresentId = user.getId() + 1;

        assertThrows(NotFoundException.class, () -> {
            userController.getUserFriends(notPresentId);
        });
    }

    @Test
    public void removeFriend_whenUserHasFriend_returnsUserWithRemovedFriend() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var userId = user.getId();

        var friend1 = userController.create(new NewUserRequest("m@mail.ru", "cusco",
                "phil", LocalDate.parse("1997-02-12")));
        // добавляем юзеру с id 1 друга с id 2
        userController.addFriend(userId, friend1.getId());

        assertEquals(1, user.getFriends().size());

        userController.removeFriend(userId, friend1.getId());

        assertEquals(0, user.getFriends().size());
    }

    @Test
    public void removeFriend_whenUserHasNoFriends_notFriendRemove() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var userId = user.getId();

        var notUserFriend = userController.create(new NewUserRequest( "m@mail.ru", "cusco",
                "phil", LocalDate.parse("1997-02-12")));
        var notUserFriendId = notUserFriend.getId();

        userController.removeFriend(userId, notUserFriendId);

        assertEquals(0, user.getFriends().size());
    }

    @Test
    public void removeFriend_whenUserNotPresent_throwsException() {
        var userFriend = userController.create(new NewUserRequest( "m@mail.ru", "cusco",
                "phil", LocalDate.parse("1997-02-12")));
        var userFriendId = userFriend.getId();
        // создаём id не существующего юзера
        var notPresentUserId = userFriendId + 1;

        assertThrows(NotFoundException .class, () -> {
            userController.removeFriend(notPresentUserId, userFriendId);
        });
    }

    @Test
    public void removeFriend_whenFriendNotPresent_throwsException() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var userId = user.getId();
        // создаём id не существующего друга
        var notPresentFriendId = userId + 1;

        assertThrows(NotFoundException.class, () -> {
            userController.removeFriend(userId, notPresentFriendId);
        });
    }

    @Test
    public void getCommonFriends_returnsUsersCommonFriendsCollection() {
        var user = userController.create(new NewUserRequest( "mail@mail.ru", "dolore",
                "john", LocalDate.parse("1991-08-20")));
        var userId = user.getId();
        var otherUser = userController.create(new NewUserRequest( "yandex@mail.ru", "cusco",
                "phil", LocalDate.parse("1997-02-12")));
        var otherUserId = otherUser.getId();

        // у юзеров user и otherUser будет один общий друг commonFriend
        var commonFriend = userController.create(new NewUserRequest( "Dan70@yahoo.com", "cute",
                "alice", LocalDate.parse("1974-12-17")));
        userController.addFriend(userId, commonFriend.getId());
        userController.addFriend(otherUserId, commonFriend.getId());

        // юзер с идентификатором userFriend будет другом только юзера user
        var userFriend = userController.create(new NewUserRequest("olga29@gmail.com", "salma",
                "olya", LocalDate.parse("2004-10-27")));
        userController.addFriend(userId, userFriend.getId());

        var commonFriends = userController.getCommonFriends(userId, otherUserId);

        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(commonFriend));
    }
}