//package ru.yandex.practicum.filmorate.dal;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.yandex.practicum.filmorate.dal.mappers.FriendshipRowMapper;
//import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
//import ru.yandex.practicum.filmorate.dal.user.UserDbStorage;
//import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.Collection;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@JdbcTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Import({UserDbStorage.class, UserRowMapper.class, FriendshipRowMapper.class})
//public class UserDbStorageTest {
//    private final UserRowMapper userMapper;
//    private final FriendshipRowMapper friendshipMapper;
//    private final UserDbStorage userStorage;
//
//    @Test
//    @DirtiesContext
//    void testCreateUser() {
//        var user = new User();
//        user.setEmail("test@example.com");
//        user.setLogin("testUser");
//        user.setName("Test");
//        user.setBirthday(LocalDate.of(1990, 1, 1));
//
//        User savedUser = userStorage.create(user);
//
//        assertThat(savedUser).isNotNull();
//        assertThat(savedUser).hasFieldOrPropertyWithValue("email", "test@example.com");
//        assertThat(savedUser).hasFieldOrPropertyWithValue("login", "testUser");
//        assertThat(savedUser).hasFieldOrPropertyWithValue("name", "Test");
//        assertThat(savedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
//    }
//
//    @Test
//    @DirtiesContext
//    void testFindUserById() {
//        var user = new User();
//        user.setEmail("test@example.com");
//        user.setLogin("testUser");
//        user.setName("Test");
//        user.setBirthday(LocalDate.of(1990, 1, 1));
//        User savedUser = userStorage.create(user);
//
//        User foundUser = userStorage.findById(savedUser.getId());
//
//        assertThat(foundUser).isNotNull();
//        assertThat(foundUser).hasFieldOrPropertyWithValue("email", "test@example.com");
//        assertThat(savedUser).hasFieldOrPropertyWithValue("login", "testUser");
//        assertThat(savedUser).hasFieldOrPropertyWithValue("name", "Test");
//        assertThat(savedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
//    }
//
//    @Test
//    @DirtiesContext
//    void testUpdateUser() {
//        var user = new User();
//        user.setEmail("test@example.com");
//        user.setLogin("testUser");
//        user.setName("Test");
//        user.setBirthday(LocalDate.of(1990, 1, 1));
//        User savedUser = userStorage.create(user);
//
//        var updateRequest = new UpdateUserRequest();
//        updateRequest.setName("Updated Name");
//        userStorage.update(savedUser, updateRequest);
//
//        User updatedUser = userStorage.findById(savedUser.getId());
//
//        assertThat(updatedUser).isNotNull();
//        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "test@example.com");
//        assertThat(updatedUser).hasFieldOrPropertyWithValue("login", "testUser");
//        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated Name");
//        assertThat(updatedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
//    }
//
//    @Test
//    @DirtiesContext
//    void testGetAllUsers() {
//        userStorage.create(new User(0L, "email", "login", "name", LocalDate.parse("1990-01-01")));
//        userStorage.create(new User(0L, "email1", "login1", "name1", LocalDate.parse("1991-01-01")));
//        userStorage.create(new User(0L, "email2", "login2", "name2", LocalDate.parse("1992-01-01")));
//
//        Collection<User> users = userStorage.getAll();
//
//        assertThat(users).isNotNull();
//        assertThat(users.size()).isEqualTo(3);
//    }
//
//    @Test
//    @DirtiesContext
//    void testAddFriend() {
//        var user = new User(0L, "email", "login", "name", LocalDate.parse("1990-01-01"));
//        var friend = new User(0L, "email1", "login1", "name1", LocalDate.parse("1991-01-01"));
//        userStorage.create(user);
//        userStorage.create(friend);
//
//        userStorage.addFriend(user, friend.getId());
//
//        User userWithFriend = userStorage.findById(user.getId());
//        assertThat(userWithFriend).isNotNull();
//        assertThat(userWithFriend.getFriends().size()).isEqualTo(1);
//
//        User userWithNoFriend = userStorage.findById(friend.getId());
//        assertThat(userWithNoFriend).isNotNull();
//        assertThat(userWithNoFriend.getFriends().size()).isEqualTo(0);
//    }
//
//    @Test
//    @DirtiesContext
//    void testRemoveFriend() {
//        var user = new User(0L, "email", "login", "name", LocalDate.parse("1990-01-01"));
//        var friendId = 2L;
//        user.getFriends().add(friendId);
//        userStorage.create(user);
//
//        userStorage.removeFriend(user, friendId);
//
//        User userWithFriendRemoved = userStorage.findById(user.getId());
//        assertThat(userWithFriendRemoved).isNotNull();
//        assertThat(userWithFriendRemoved.getFriends().size()).isEqualTo(0);
//    }
//
//    @Test
//    @DirtiesContext
//    void testGetUserFriends() {
//        var user = new User(0L, "email", "login", "name", LocalDate.parse("1990-01-01"));
//        var friend1 = new User(0L, "email2", "login2", "name2", LocalDate.parse("1991-01-01"));
//        var friend2 = new User(0L, "email3", "login3", "name3", LocalDate.parse("1992-01-01"));
//        userStorage.create(user);
//        userStorage.create(friend1);
//        userStorage.create(friend2);
//        userStorage.addFriend(user, friend1.getId());
//        userStorage.addFriend(user, friend2.getId());
//
//        Collection<User> friends = userStorage.getUserFriends(user.getId());
//
//        assertThat(friends).isNotNull();
//        assertThat(friends.size()).isEqualTo(2);
//    }
//}
