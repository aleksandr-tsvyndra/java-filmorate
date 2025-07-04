package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.UserUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User findById(long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User oldUser, UpdateUserRequest newUser) {
        UserUtils.updateFields(oldUser, newUser, users.values());
        return oldUser;
    }

    @Override
    public void addFriend(User user, Long friendId) {
        user.getFriends().add(friendId);
    }

    @Override
    public void removeFriend(User user, Long friendId) {
        user.getFriends().remove(friendId);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        Set<Long> userFriends = users.get(userId).getFriends();
        return users.values()
                .stream()
                .filter(u -> userFriends.contains(u.getId()))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
