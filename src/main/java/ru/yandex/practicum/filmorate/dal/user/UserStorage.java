package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User findById(long id);

    User update(User user);

    Collection<User> getUserFriends(Long userId);
}
