package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseStorage;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.DuplicateKeyException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.UserUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String FIND_USER_FRIENDS_QUERY = "SELECT u.* FROM users AS u " +
            "JOIN friends AS f ON u.user_id = f.friend_id WHERE f.user_id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT * FROM users WHERE user_id IN (SELECT f.user_id " +
            "FROM friends AS f WHERE f.user_id = ? AND f.friend_id = ?)";

    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc,
            RowMapper<User> userMapper,
            RowMapper<Friendship> friendshipMapper) {
        super(jdbc, userMapper);
        friendshipStorage = new FriendshipDbStorage(jdbc, friendshipMapper);
    }

    @Override
    public Collection<User> getAll() {
        return findMany(FIND_ALL_QUERY)
                .stream()
                .peek(u -> u.getFriends().addAll(setUserFriends(u.getId())))
                .toList();
    }

    @Override
    public User findById(long id) {
        Optional<User> result = findOne(FIND_BY_ID_QUERY, id);
        if (result.isPresent()) {
            User user = result.get();
            user.getFriends().addAll(setUserFriends(user.getId()));
            return user;
        }
        return null;
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User oldUser, UpdateUserRequest newUser) {
        UserUtils.updateFields(oldUser, newUser, getAll());
        update(
                UPDATE_QUERY,
                oldUser.getEmail(),
                oldUser.getLogin(),
                oldUser.getName(),
                oldUser.getBirthday(),
                oldUser.getId()
        );
        return oldUser;
    }

    @Override
    public void addFriend(User user, Long friendId) {
        if (isFriend(user.getId(), friendId)) {
            String message = String.format("Юзер с id %d уже в друзьях у юзера с id %d", friendId, user.getId());
            throw new DuplicateKeyException(message);
        }
        friendshipStorage.addFriend(user.getId(), friendId);
    }

    @Override
    public void removeFriend(User user, Long friendId) {
        friendshipStorage.removeFriend(user.getId(), friendId);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        return findMany(FIND_USER_FRIENDS_QUERY, userId);
    }

    private boolean isFriend(long userId, long friendId) {
        return findOne(FIND_FRIENDS_QUERY, userId, friendId).isPresent();
    }

    private Set<Long> setUserFriends(Long userId) {
        return getUserFriends(userId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}
