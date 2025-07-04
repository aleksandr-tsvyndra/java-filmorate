package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User findById(long id);

    User update(User oldUser, UpdateUserRequest newUser);

    void addFriend(User user, Long friendId);

    void removeFriend(User user, Long friendId);

    Collection<User> getUserFriends(Long userId);
}
